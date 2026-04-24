package project.vilsoncake.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.vilsoncake.common.entities.SpecificAircraftFlightNotificationEntity;

public interface SpecificAircraftFlightNotificationRepository
    extends JpaRepository<SpecificAircraftFlightNotificationEntity, String> {}
