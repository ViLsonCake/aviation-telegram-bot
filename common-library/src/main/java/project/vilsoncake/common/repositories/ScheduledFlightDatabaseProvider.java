package project.vilsoncake.common.repositories;

import static project.vilsoncake.common.utils.BotMessagesUtils.getValueOrUnknown;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import project.vilsoncake.common.entities.AirportEntity;
import project.vilsoncake.common.entities.ScheduledFlightEntity;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;
import project.vilsoncake.common.models.ScheduledFlight;

@RequiredArgsConstructor
public class ScheduledFlightDatabaseProvider {

  private final ScheduledFlightRepository scheduledFlightRepository;
  private final AirportRepository airportRepository;
  private final WidebodyAircraftRepository widebodyAircraftRepository;

  @Transactional
  public ScheduledFlightEntity getOrCreate(
      ScheduledFlight scheduledFlight, AirportEntity destinationAirport) {
    return scheduledFlightRepository
        .findById(
            ScheduledFlightEntity.constructId(
                scheduledFlight.getRowId(), scheduledFlight.getScheduledDepartureTime()))
        .orElseGet(
            () -> scheduledFlightRepository.save(build(scheduledFlight, destinationAirport)));
  }

  private ScheduledFlightEntity build(
      ScheduledFlight scheduledFlight, AirportEntity destinationAirport) {
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

    ZoneId airportZone = ZoneId.of(destinationAirport.getTimezone());
    ZonedDateTime estimatedArrivalTime =
        scheduledFlight.getEstimatedArrivalTime() != null
            ? ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(scheduledFlight.getEstimatedArrivalTime()), airportZone)
            : null;

    return ScheduledFlightEntity.builder()
        .withRowId(getValueOrUnknown(scheduledFlight.getRowId()))
        .withAircraft(aircraft)
        .withAirlineName(getValueOrUnknown(scheduledFlight.getAirlineName()))
        .withOriginAirport(originAirport)
        .withDestinationAirport(destinationAirport)
        .withCallsign(getValueOrUnknown(scheduledFlight.getCallsign()))
        .withRegistration(getValueOrUnknown(scheduledFlight.getRegistration()))
        .withLive(scheduledFlight.getLive())
        .withStatus(getValueOrUnknown(scheduledFlight.getStatus()))
        .withScheduledDepartureTime(
            ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(scheduledFlight.getScheduledDepartureTime()), airportZone))
        .withScheduledArrivalTime(
            ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(scheduledFlight.getScheduledArrivalTime()), airportZone))
        .withEstimatedArrivalTime(estimatedArrivalTime)
        .build();
  }
}
