package project.vilsoncake.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlightStatus {
  SCHEDULED("Scheduled"),
  ESTIMATED("Estimated"),
  LANDED("Landed"),
  DIVERTED("Diverted"),
  DELAYED("Delayed"),
  CANCELLED("Canceled");

  private final String flightradarName;
}
