package project.vilsoncake.common.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.vilsoncake.common.entities.AirportEntity;

public interface AirportRepository extends JpaRepository<AirportEntity, String> {

  @Query(
      value = "SELECT * FROM airports WHERE icao = :airportCode OR iata = :airportCode",
      nativeQuery = true)
  Optional<AirportEntity> findByAirportCode(@Param("airportCode") String airportCode);
}
