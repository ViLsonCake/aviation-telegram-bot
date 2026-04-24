package project.vilsoncake.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.vilsoncake.common.entities.AircraftFamilyEntity;

public interface AircraftFamilyRepository extends JpaRepository<AircraftFamilyEntity, String> {}
