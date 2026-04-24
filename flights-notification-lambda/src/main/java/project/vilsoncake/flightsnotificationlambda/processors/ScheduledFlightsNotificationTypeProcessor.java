package project.vilsoncake.flightsnotificationlambda.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;
import project.vilsoncake.flightsnotificationlambda.models.AirportRequest;
import project.vilsoncake.flightsnotificationlambda.models.AirportResponse;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationType;
import project.vilsoncake.flightsnotificationlambda.services.UserNotificationsSender;
import project.vilsoncake.flightsnotificationlambda.services.adapters.FlightradarApiLambdaAdapter;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class ScheduledFlightsNotificationTypeProcessor implements NotificationTypeProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider;
  private final FlightradarApiLambdaAdapter flightradarApiLambdaAdapter;
  private final UserNotificationsSender userNotificationsSender;

  @Override
  public FlightsNotificationType getNotificationType() {
    return FlightsNotificationType.SCHEDULED_FLIGHTS;
  }

  @Override
  public void process() {
    log.info("Start scheduled flights notifications processing");

    Set<UserEntity> eligibleUsers =
        userDatabaseProvider.getEligibleUsersToSendScheduledFlightsNotifications();
    Set<String> uniqueAirportsIcao = getUniqueAirportsIcao(eligibleUsers);

    log.info(
        "Found {} eligible users for scheduled flights notifications and {} unique airports",
        eligibleUsers.size(),
        uniqueAirportsIcao.size());

    List<WideBodyAircraftEntity> allWideBodyAircraft =
        widebodyAircraftDatabaseProvider.getAllWideBodyAircraft();
    // TODO: replace with individual aircraft codes for each user once feature implemented
    List<String> allWideBodyAircraftCodes =
        allWideBodyAircraft.stream().map(WideBodyAircraftEntity::getCode).toList();
    List<AirportRequest> airportRequests =
        buildAirportRequests(uniqueAirportsIcao, allWideBodyAircraftCodes);

    Map<String, AirportResponse> filteredFlightsForAirports =
        flightradarApiLambdaAdapter.getFilteredFlightsForAirports(airportRequests);

    notifyUsers(eligibleUsers, filteredFlightsForAirports);

    log.info("Finished processing scheduled flights notifications");
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
      userNotificationsSender.notifyScheduledFlights(user, airportResponse);
    }
  }

  private Set<String> getUniqueAirportsIcao(Set<UserEntity> users) {
    return users.stream().map(user -> user.getAirport().getIcao()).collect(Collectors.toSet());
  }

  private List<AirportRequest> buildAirportRequests(
      Set<String> airports, List<String> aircraftFilter) {
    List<AirportRequest> airportRequests = new ArrayList<>();

    airports.forEach(
        airport -> {
          airportRequests.add(
              AirportRequest.builder()
                  .withAirportCode(airport)
                  .withAircraftFilterCodes(aircraftFilter)
                  .build());
        });

    return airportRequests;
  }
}
