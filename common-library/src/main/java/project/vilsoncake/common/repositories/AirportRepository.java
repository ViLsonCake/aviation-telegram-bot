package project.vilsoncake.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.vilsoncake.common.entities.AirportEntity;

import java.util.Optional;

public interface AirportRepository extends JpaRepository<AirportEntity, String> {

  @Query(value = "SELECT * FROM airports WHERE icao = :airportCode OR iata = :airportCode", nativeQuery = true)
  Optional<AirportEntity> findByAirportCode(String airportCode);
}
