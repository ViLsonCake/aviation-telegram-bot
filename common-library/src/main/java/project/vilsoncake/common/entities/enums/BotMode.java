package project.vilsoncake.common.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BotMode {
  DEFAULT("Default"),
  ONLY_SCHEDULED_FLIGHTS("Only Scheduled Flights"),
  ONLY_SPECIFIC_AIRCRAFT("Only Specific Aircraft"),
  MUTE("Mute");

  private final String label;
}
