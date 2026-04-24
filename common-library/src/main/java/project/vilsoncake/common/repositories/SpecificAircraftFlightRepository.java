package project.vilsoncake.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.vilsoncake.common.entities.SpecificAircraftFlightEntity;

public interface SpecificAircraftFlightRepository
    extends JpaRepository<SpecificAircraftFlightEntity, String> {}
