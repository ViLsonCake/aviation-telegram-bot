package project.vilsoncake.common.repositories;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.entities.AirportEntity;

@RequiredArgsConstructor(staticName = "create")
public class AirportDatabaseProvider {

  private final AirportRepository airportRepository;

  public Optional<AirportEntity> getAirportByCode(String airportCode) {
    return airportRepository.findByAirportCode(airportCode);
  }
}
