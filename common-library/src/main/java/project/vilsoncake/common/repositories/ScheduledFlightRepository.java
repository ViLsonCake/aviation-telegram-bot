package project.vilsoncake.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.vilsoncake.common.entities.ScheduledFlightEntity;

public interface ScheduledFlightRepository extends JpaRepository<ScheduledFlightEntity, String> {}
