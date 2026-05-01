package project.vilsoncake.common.repositories;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.vilsoncake.common.entities.ScheduledFlightNotificationEntity;
import project.vilsoncake.common.entities.enums.BotMode;

public interface ScheduledFlightNotificationRepository
    extends JpaRepository<ScheduledFlightNotificationEntity, UUID> {

  boolean existsByScheduledFlightId(String scheduledFlightId);

  @Query(
      """
      SELECT sfn FROM ScheduledFlightNotificationEntity sfn
      JOIN FETCH sfn.scheduledFlight sf
      JOIN FETCH sf.destinationAirport
      JOIN FETCH sfn.user u
      WHERE TYPE(sfn) = ScheduledFlightNotificationEntity
      AND u.state = 'ALL_SET'
      AND u.botMode IN :botModes
      AND sf.destinationAirport = u.airport
      AND sf.scheduledDepartureTime > :cutoff
      AND (
        sf.status NOT IN :completedStatuses
        OR (sf.status = :landedStatus AND sfn.notifiedLanded = false)
      )
      """)
  List<ScheduledFlightNotificationEntity> findActiveForEligibleUsers(
      @Param("botModes") Set<BotMode> botModes,
      @Param("completedStatuses") Set<String> completedStatuses,
      @Param("landedStatus") String landedStatus,
      @Param("cutoff") ZonedDateTime cutoff);
}
