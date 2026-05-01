package project.vilsoncake.common.repositories;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import project.vilsoncake.common.entities.ScheduledFlightEntity;
import project.vilsoncake.common.entities.ScheduledFlightNotificationEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.BotMode;
import project.vilsoncake.common.models.FlightStatus;
import project.vilsoncake.common.models.ScheduledFlightNotificationFlags;

@RequiredArgsConstructor
public class ScheduledFlightNotificationDatabaseProvider {

  private final ScheduledFlightNotificationRepository scheduledFlightNotificationRepository;

  public boolean isNotificationSent(String flightId) {
    return scheduledFlightNotificationRepository.existsByScheduledFlightId(flightId);
  }

  public List<ScheduledFlightNotificationEntity> findActiveForEligibleUsers() {
    return scheduledFlightNotificationRepository.findActiveForEligibleUsers(
        Set.of(BotMode.DEFAULT, BotMode.ONLY_SCHEDULED_FLIGHTS),
        Set.of(
            FlightStatus.LANDED.getFlightradarName(), FlightStatus.CANCELLED.getFlightradarName()),
        FlightStatus.LANDED.getFlightradarName(),
        ZonedDateTime.now().minusHours(24));
  }

  @Transactional
  public void saveNotification(
      ScheduledFlightEntity scheduledFlightEntity,
      UserEntity userEntity,
      ScheduledFlightNotificationFlags flags) {
    ScheduledFlightNotificationEntity notification =
        ScheduledFlightNotificationEntity.builder()
            .withUser(userEntity)
            .withScheduledFlight(scheduledFlightEntity)
            .withNotifiedAt(ZonedDateTime.now())
            .withNotifiedDelayed(flags.notifiedDelayed())
            .withNotifiedCancelled(flags.notifiedCancelled())
            .withNotifiedDiverted(flags.notifiedDiverted())
            .withNotifiedArrivingSoon(flags.notifiedArrivingSoon())
            .withNotifiedLive(false)
            .withNotifiedLanded(flags.notifiedLanded())
            .build();
    scheduledFlightNotificationRepository.save(notification);
  }

  @Transactional
  public void updateStatusChangeFlags(ScheduledFlightNotificationEntity entity) {
    scheduledFlightNotificationRepository.save(entity);
  }
}
