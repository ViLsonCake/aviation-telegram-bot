package project.vilsoncake.flightsnotificationlambda.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;
import project.vilsoncake.common.models.AirportRequest;
import project.vilsoncake.common.models.AirportResponse;
import project.vilsoncake.common.models.ScheduledFlight;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterDatabaseProvider;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;
import project.vilsoncake.common.services.adapters.FlightradarApiLambdaAdapter;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationType;
import project.vilsoncake.flightsnotificationlambda.services.ScheduledFlightsNotificationSender;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class ScheduledFlightsNotificationTypeProcessor implements NotificationTypeProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider;
  private final UserAircraftFamilyFilterDatabaseProvider userAircraftFamilyFilterDatabaseProvider;
  private final FlightradarApiLambdaAdapter flightradarApiLambdaAdapter;
  private final ScheduledFlightsNotificationSender scheduledFlightsNotificationSender;

  @Override
  public FlightsNotificationType getNotificationType() {
    return FlightsNotificationType.SCHEDULED_FLIGHTS;
  }

  @Override
  public void process() {
    log.info("Start scheduled flights notifications processing");

    Set<UserEntity> eligibleUsers =
        userDatabaseProvider.getEligibleUsersToSendScheduledFlightsNotifications();

    log.info("Found {} eligible users for scheduled flights notifications", eligibleUsers.size());

    List<WideBodyAircraftEntity> allWideBodyAircraft =
        widebodyAircraftDatabaseProvider.getAllWideBodyAircraft();
    Set<String> allWideBodyAircraftCodes =
        allWideBodyAircraft.stream()
            .map(WideBodyAircraftEntity::getCode)
            .collect(Collectors.toSet());

    Map<String, Set<String>> aircraftCodesByAirport =
        buildAircraftCodesByAirport(eligibleUsers, allWideBodyAircraftCodes);

    log.info("Querying Flightradar API for {} unique airports", aircraftCodesByAirport.size());

    List<AirportRequest> airportRequests = buildAirportRequests(aircraftCodesByAirport);
    Map<String, AirportResponse> filteredFlightsForAirports =
        flightradarApiLambdaAdapter.getFilteredFlightsForAirports(airportRequests);
    log.info("Received aircraft from Flightradar API");

    log.info("Starting notifying users for scheduled flights");
    notifyUsers(eligibleUsers, filteredFlightsForAirports);

    log.info("Finished processing scheduled flights notifications");
  }

  private Map<String, Set<String>> buildAircraftCodesByAirport(
      Set<UserEntity> users, Set<String> allCodes) {
    Map<String, Set<String>> result = new HashMap<>();

    for (UserEntity user : users) {
      String icao = user.getAirport().getIcao();
      Set<String> userFamilyCodes =
          userAircraftFamilyFilterDatabaseProvider.getFilterFamilyCodes(user);

      Set<String> userAircraftCodes;
      if (userFamilyCodes.isEmpty()) {
        userAircraftCodes = allCodes;
      } else {
        userAircraftCodes =
            widebodyAircraftDatabaseProvider
                .getWideBodyAircraftByFamilies(new ArrayList<>(userFamilyCodes))
                .stream()
                .map(WideBodyAircraftEntity::getCode)
                .collect(Collectors.toSet());
      }

      result.computeIfAbsent(icao, k -> new HashSet<>()).addAll(userAircraftCodes);
    }

    return result;
  }

  private void notifyUsers(
      Set<UserEntity> users, Map<String, AirportResponse> filteredFlightsForAirports) {
    for (UserEntity user : users) {
      String userAirportIcao = user.getAirport().getIcao();

      if (!filteredFlightsForAirports.containsKey(userAirportIcao)) {
        log.warn(
            "User {} didn't receive flights for its airport from response", user.getUsername());
        continue;
      }

      AirportResponse airportResponse = filteredFlightsForAirports.get(userAirportIcao);
      AirportResponse userFilteredResponse = filterResponseForUser(user, airportResponse);

      scheduledFlightsNotificationSender.notifyScheduledFlights(user, userFilteredResponse);
    }
  }

  private AirportResponse filterResponseForUser(UserEntity user, AirportResponse airportResponse) {
    Set<String> userFamilyCodes =
        userAircraftFamilyFilterDatabaseProvider.getFilterFamilyCodes(user);

    if (userFamilyCodes.isEmpty()) {
      return airportResponse;
    }

    Set<String> userAircraftCodes =
        widebodyAircraftDatabaseProvider
            .getWideBodyAircraftByFamilies(new ArrayList<>(userFamilyCodes))
            .stream()
            .map(WideBodyAircraftEntity::getCode)
            .collect(Collectors.toSet());

    List<ScheduledFlight> filteredFlights =
        airportResponse.getFlights().stream()
            .filter(flight -> userAircraftCodes.contains(flight.getAircraftCode()))
            .toList();

    return new AirportResponse(
        airportResponse.getArrivalsCount(), filteredFlights.size(), filteredFlights);
  }

  private List<AirportRequest> buildAirportRequests(
      Map<String, Set<String>> aircraftCodesByAirport) {
    List<AirportRequest> airportRequests = new ArrayList<>();

    aircraftCodesByAirport.forEach(
        (airportCode, aircraftCodes) ->
            airportRequests.add(
                AirportRequest.builder()
                    .withAirportCode(airportCode)
                    .withAircraftFilterCodes(new ArrayList<>(aircraftCodes))
                    .build()));

    return airportRequests;
  }
}
