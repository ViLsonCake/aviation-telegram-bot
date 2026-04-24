package project.vilsoncake.common.repositories;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import project.vilsoncake.common.configurations.NotificationsConfig;
import project.vilsoncake.common.entities.AirportEntity;
import project.vilsoncake.common.entities.ScheduledFlightEntity;
import project.vilsoncake.common.entities.ScheduledFlightNotificationEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;
import project.vilsoncake.common.models.FlightStatus;
import project.vilsoncake.common.models.ScheduledFlight;

@RequiredArgsConstructor
public class ScheduledFlightNotificationDatabaseProvider {

  private final ScheduledFlightNotificationRepository scheduledFlightNotificationRepository;
  private final ScheduledFlightRepository scheduledFlightRepository;
  private final AirportRepository airportRepository;
  private final WidebodyAircraftRepository widebodyAircraftRepository;
  private final NotificationsConfig notificationsConfig;

  public boolean isNotificationSent(String flightId) {
    return scheduledFlightNotificationRepository.existsByScheduledFlightId(flightId);
  }

  @Transactional
  public void saveNotification(ScheduledFlight scheduledFlight, UserEntity userEntity) {
    WideBodyAircraftEntity aircraft =
        widebodyAircraftRepository
            .findById(scheduledFlight.getAircraftCode())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Aircraft not found: " + scheduledFlight.getAircraftCode()));

    AirportEntity originAirport =
        airportRepository
            .findByAirportCode(scheduledFlight.getOriginAirportIcao())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Origin airport not found: " + scheduledFlight.getOriginAirportIcao()));

    ScheduledFlightEntity scheduledFlightEntity =
        getOrCreateScheduledFlightEntity(scheduledFlight, aircraft, originAirport, userEntity);

    ScheduledFlightNotificationEntity notification =
        buildScheduledFlightNotificationEntity(scheduledFlightEntity, userEntity);
    scheduledFlightNotificationRepository.save(notification);
  }

  private ScheduledFlightNotificationEntity buildScheduledFlightNotificationEntity(
      ScheduledFlightEntity scheduledFlightEntity, UserEntity userEntity) {
    String status = scheduledFlightEntity.getStatus();
    boolean live = scheduledFlightEntity.getLive();
    ZoneId airportZone = ZoneId.of(scheduledFlightEntity.getDestinationAirport().getTimezone());
    boolean notifiedArrivingSoon = false;

    if (live) {
      int minutesUntilArrival =
          (int)
              ZonedDateTime.now(airportZone)
                  .until(scheduledFlightEntity.getEstimatedArrivalTime(), ChronoUnit.MINUTES);

      if (minutesUntilArrival <= notificationsConfig.getArrivingSoonRemainingMinutes()) {
        notifiedArrivingSoon = true;
      }
    }

    return ScheduledFlightNotificationEntity.builder()
        .withUser(userEntity)
        .withScheduledFlight(scheduledFlightEntity)
        .withNotifiedAt(ZonedDateTime.now())
        .withNotifiedDelayed(status.startsWith(FlightStatus.DELAYED.getFlightradarName()))
        .withNotifiedCancelled(status.startsWith(FlightStatus.CANCELLED.getFlightradarName()))
        .withNotifiedDiverted(status.startsWith(FlightStatus.DIVERTED.getFlightradarName()))
        .withNotifiedArrivingSoon(notifiedArrivingSoon)
        .build();
  }

  private ScheduledFlightEntity buildScheduledFlightEntity(
      ScheduledFlight scheduledFlight,
      WideBodyAircraftEntity aircraft,
      AirportEntity originAirport,
      AirportEntity destinationAirport) {
    ZoneId airportZone = ZoneId.of(destinationAirport.getTimezone());
    ZonedDateTime estimatedArrivalTime =
        scheduledFlight.getEstimatedArrivalTime() != null
            ? ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(scheduledFlight.getEstimatedArrivalTime()), airportZone)
            : null;
    return ScheduledFlightEntity.builder()
        .withId(scheduledFlight.getId())
        .withAircraft(aircraft)
        .withAirlineName(scheduledFlight.getAirlineName())
        .withOriginAirport(originAirport)
        .withDestinationAirport(destinationAirport)
        .withCallsign(scheduledFlight.getCallsign())
        .withRegistration(scheduledFlight.getRegistration())
        .withLive(scheduledFlight.getLive())
        .withStatus(scheduledFlight.getStatus())
        .withScheduledDepartureTime(
            ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(scheduledFlight.getScheduledDepartureTime()), airportZone))
        .withScheduledArrivalTime(
            ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(scheduledFlight.getScheduledArrivalTime()), airportZone))
        .withEstimatedArrivalTime(estimatedArrivalTime)
        .build();
  }

  private ScheduledFlightEntity getOrCreateScheduledFlightEntity(
      ScheduledFlight scheduledFlight,
      WideBodyAircraftEntity aircraft,
      AirportEntity originAirport,
      UserEntity userEntity) {
    return scheduledFlightRepository
        .findById(scheduledFlight.getId())
        .orElseGet(
            () ->
                scheduledFlightRepository.save(
                    buildScheduledFlightEntity(
                        scheduledFlight, aircraft, originAirport, userEntity.getAirport())));
  }
}
