package project.vilsoncake.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BotCommand {
  START("/start"),
  PING("/ping"),
  AIRPORT("/airport");

  private final String command;
}
