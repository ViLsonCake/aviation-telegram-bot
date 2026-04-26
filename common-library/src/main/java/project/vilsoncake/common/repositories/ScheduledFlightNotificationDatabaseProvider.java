package project.vilsoncake.common.repositories;

import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import project.vilsoncake.common.entities.ScheduledFlightEntity;
import project.vilsoncake.common.entities.ScheduledFlightNotificationEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.models.ScheduledFlightNotificationFlags;

@RequiredArgsConstructor
public class ScheduledFlightNotificationDatabaseProvider {

  private final ScheduledFlightNotificationRepository scheduledFlightNotificationRepository;

  public boolean isNotificationSent(String flightId) {
    return scheduledFlightNotificationRepository.existsByScheduledFlightId(flightId);
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
            .build();
    scheduledFlightNotificationRepository.save(notification);
  }
}
