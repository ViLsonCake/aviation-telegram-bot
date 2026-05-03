package project.vilsoncake.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BotCommand {
  START("/start"),
  PING("/ping"),
  AIRPORT("/airport"),
  MODE("/mode"),
  AIRCRAFT("/aircraft");

  private final String command;
}
