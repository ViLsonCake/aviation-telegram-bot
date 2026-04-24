package project.vilsoncake.common.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.vilsoncake.common.entities.ScheduledFlightNotificationEntity;

public interface ScheduledFlightNotificationRepository
    extends JpaRepository<ScheduledFlightNotificationEntity, UUID> {

  boolean existsByScheduledFlightId(String scheduledFlightId);
}
