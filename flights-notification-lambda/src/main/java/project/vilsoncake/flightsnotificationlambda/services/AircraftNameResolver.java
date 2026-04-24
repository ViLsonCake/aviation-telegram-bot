package project.vilsoncake.flightsnotificationlambda.services;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;

@RequiredArgsConstructor(staticName = "create")
public class AircraftNameResolver {

  private final WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider;

  private static final String UNKNOWN_AIRCRAFT_NAME = "Unknown";

  public String getAircraftName(String aircraftCode, String aircraftName) {
    if (aircraftName != null && !aircraftName.isBlank()) {
      return aircraftName;
    }

    Optional<WideBodyAircraftEntity> wideBodyAircraft =
        widebodyAircraftDatabaseProvider.getWideBodyAircraftByCode(aircraftCode);

    if (wideBodyAircraft.isEmpty()) {
      return UNKNOWN_AIRCRAFT_NAME;
    }

    return wideBodyAircraft.get().getName();
  }
}
