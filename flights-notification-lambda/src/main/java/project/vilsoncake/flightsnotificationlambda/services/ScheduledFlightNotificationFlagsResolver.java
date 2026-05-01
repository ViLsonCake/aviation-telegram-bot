package project.vilsoncake.flightsnotificationlambda.services;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.configurations.NotificationsConfig;
import project.vilsoncake.common.entities.ScheduledFlightEntity;
import project.vilsoncake.common.models.FlightStatus;
import project.vilsoncake.common.models.ScheduledFlightNotificationFlags;

@RequiredArgsConstructor(staticName = "create")
public class ScheduledFlightNotificationFlagsResolver {

  private final NotificationsConfig notificationsConfig;

  public ScheduledFlightNotificationFlags resolve(ScheduledFlightEntity scheduledFlightEntity) {
    String status = scheduledFlightEntity.getStatus();
    boolean notifiedArrivingSoon = false;

    if (scheduledFlightEntity.getLive()
        && scheduledFlightEntity.getEstimatedArrivalTime() != null) {
      int minutesUntilArrival =
          (int)
              ZonedDateTime.now(scheduledFlightEntity.getEstimatedArrivalTime().getZone())
                  .until(scheduledFlightEntity.getEstimatedArrivalTime(), ChronoUnit.MINUTES);

      if (minutesUntilArrival <= notificationsConfig.getArrivingSoonRemainingMinutes()) {
        notifiedArrivingSoon = true;
      }
    }

    return new ScheduledFlightNotificationFlags(
        status.startsWith(FlightStatus.DELAYED.getFlightradarName()),
        status.startsWith(FlightStatus.CANCELLED.getFlightradarName()),
        status.startsWith(FlightStatus.DIVERTED.getFlightradarName()),
        notifiedArrivingSoon,
        status.startsWith(FlightStatus.LANDED.getFlightradarName()));
  }
}
