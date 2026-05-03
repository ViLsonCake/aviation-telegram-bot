package project.vilsoncake.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AircraftFamily {
  BOEING_747("Boeing 747"),
  BOEING_777("Boeing 777"),
  BOEING_787("Boeing 787"),
  BOEING_767("Boeing 767"),
  BOEING_757("Boeing 757"),
  BOEING_C17("Boeing C-17"),
  AIRBUS_A380("Airbus A380"),
  AIRBUS_A350("Airbus A350"),
  AIRBUS_A340("Airbus A340"),
  AIRBUS_A330("Airbus A330"),
  AIRBUS_A310("Airbus A310"),
  AIRBUS_A300("Airbus A300"),
  AIRBUS_A400("Airbus A400M"),
  MD11("MD-11"),
  AN124("An-124"),
  C5_GALAXY("C-5 Galaxy"),
  C130_HERCULES("C-130 Hercules"),
  ILYUSHIN_IL76("Il-76");

  private final String displayName;
}
