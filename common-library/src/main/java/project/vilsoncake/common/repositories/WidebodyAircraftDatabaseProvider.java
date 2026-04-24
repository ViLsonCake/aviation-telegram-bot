package project.vilsoncake.common.repositories;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;

@RequiredArgsConstructor(staticName = "create")
public class WidebodyAircraftDatabaseProvider {

  private final WidebodyAircraftRepository widebodyAircraftRepository;

  public List<WideBodyAircraftEntity> getAllWideBodyAircraft() {
    return widebodyAircraftRepository.findAllWithFamily();
  }

  public List<WideBodyAircraftEntity> getWideBodyAircraftByFamilies(List<String> families) {
    return widebodyAircraftRepository.findAllByFamilyCodes(families);
  }

  public Optional<WideBodyAircraftEntity> getWideBodyAircraftByCode(String aircraftCode) {
    return widebodyAircraftRepository.findById(aircraftCode);
  }
}
