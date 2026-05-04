package project.vilsoncake.flightsnotificationlambda.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.entities.ScheduledFlightNotificationEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;
import project.vilsoncake.common.models.AirportRequest;
import project.vilsoncake.common.models.AirportResponse;
import project.vilsoncake.common.models.ScheduledFlight;
import project.vilsoncake.common.repositories.ScheduledFlightNotificationDatabaseProvider;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterDatabaseProvider;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;
import project.vilsoncake.common.services.adapters.FlightradarApiLambdaAdapter;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationType;
import project.vilsoncake.flightsnotificationlambda.services.FlightStatusChangeNotificationSender;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class FlightStatusChangeNotificationTypeProcessor implements NotificationTypeProcessor {

  private final ScheduledFlightNotificationDatabaseProvider
      scheduledFlightNotificationDatabaseProvider;
  private final WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider;
  private final UserAircraftFamilyFilterDatabaseProvider userAircraftFamilyFilterDatabaseProvider;
  private final FlightradarApiLambdaAdapter flightradarApiLambdaAdapter;
  private final FlightStatusChangeNotificationSender flightStatusChangeNotificationSender;

  @Override
  public FlightsNotificationType getNotificationType() {
    return FlightsNotificationType.FLIGHT_STATUS_CHANGE;
  }

  @Override
  public void process() {
    log.info("Start flight status change notifications processing");

    List<ScheduledFlightNotificationEntity> activeNotifications =
        scheduledFlightNotificationDatabaseProvider.findActiveForEligibleUsers();
    log.info(
        "Found {} active notifications",
        activeNotifications.stream()
            .map(n -> n.getScheduledFlight().getId())
            .collect(Collectors.toSet()));

    if (activeNotifications.isEmpty()) {
      log.info("No active flight notifications to check for status changes");
      return;
    }

    log.info("Found {} active flight notifications to check", activeNotifications.size());

    Set<String> uniqueAirportIcaoCodes =
        activeNotifications.stream()
            .map(n -> n.getScheduledFlight().getDestinationAirport().getIcao())
            .collect(Collectors.toSet());

    List<String> allWideBodyAircraftCodes =
        widebodyAircraftDatabaseProvider.getAllWideBodyAircraft().stream()
            .map(WideBodyAircraftEntity::getCode)
            .toList();

    List<AirportRequest> airportRequests = new ArrayList<>();
    uniqueAirportIcaoCodes.forEach(
        icao ->
            airportRequests.add(
                AirportRequest.builder()
                    .withAirportCode(icao)
                    .withAircraftFilterCodes(allWideBodyAircraftCodes)
                    .build()));

    Map<String, AirportResponse> flightsForAirports =
        flightradarApiLambdaAdapter.getFilteredFlightsForAirports(airportRequests);

    // Build rowId → ScheduledFlight lookup per airport
    Map<String, Map<String, ScheduledFlight>> rowIdByAirport = new HashMap<>();
    flightsForAirports.forEach(
        (airportIcao, airportResponse) -> {
          Map<String, ScheduledFlight> rowIdMap = new HashMap<>();
          airportResponse.getFlights().forEach(f -> rowIdMap.put(f.getRowId(), f));
          rowIdByAirport.put(airportIcao, rowIdMap);
        });

    for (ScheduledFlightNotificationEntity notification : activeNotifications) {
      String destinationIcao = notification.getScheduledFlight().getDestinationAirport().getIcao();
      Map<String, ScheduledFlight> rowIdMap = rowIdByAirport.get(destinationIcao);

      if (rowIdMap == null) {
        log.warn("No API response for airport {}, skipping", destinationIcao);
        continue;
      }

      ScheduledFlight currentFlight = rowIdMap.get(notification.getScheduledFlight().getRowId());

      if (currentFlight == null) {
        log.info(
            "Flight {} no longer in API response for airport {}, skipping",
            notification.getScheduledFlight().getRowId(),
            destinationIcao);
        continue;
      }

      if (!matchesUserAircraftFilter(notification.getUser(), currentFlight.getAircraftCode())) {
        log.info(
            "Skipping status change for flight {} - aircraft {} not in user {} filter",
            notification.getScheduledFlight().getRowId(),
            currentFlight.getAircraftCode(),
            notification.getUser().getUsername());
        continue;
      }

      flightStatusChangeNotificationSender.sendStatusChangeNotificationsIfNeeded(
          notification, currentFlight);
    }

    log.info("Finished flight status change notifications processing");
  }

  private boolean matchesUserAircraftFilter(UserEntity user, String aircraftCode) {
    Set<String> userFamilyCodes =
        userAircraftFamilyFilterDatabaseProvider.getFilterFamilyCodes(user);
    if (userFamilyCodes.isEmpty()) {
      return true;
    }
    Set<String> allowedCodes =
        widebodyAircraftDatabaseProvider
            .getWideBodyAircraftByFamilies(new ArrayList<>(userFamilyCodes))
            .stream()
            .map(WideBodyAircraftEntity::getCode)
            .collect(Collectors.toSet());
    return allowedCodes.contains(aircraftCode);
  }
}
