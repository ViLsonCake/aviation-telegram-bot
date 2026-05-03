package project.vilsoncake.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AircraftFamily {
  BOEING_747("747"),
  BOEING_777("777"),
  BOEING_787("787"),
  BOEING_767("767"),
  BOEING_757("757"),
  BOEING_C17("C-17"),
  AIRBUS_A380("A380"),
  AIRBUS_A350("A350"),
  AIRBUS_A340("A340"),
  AIRBUS_A330("A330"),
  AIRBUS_A310("A310"),
  AIRBUS_A300("A300"),
  AIRBUS_A400("A400M"),
  MD11("MD-11"),
  AN124("An-124"),
  C5_GALAXY("C-5 Galaxy"),
  C130_HERCULES("C-130"),
  ILYUSHIN_IL76("Il-76");

  private final String displayName;
}
