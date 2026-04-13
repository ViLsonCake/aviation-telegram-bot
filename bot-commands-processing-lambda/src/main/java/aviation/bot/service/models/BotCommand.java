package aviation.bot.service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BotCommand {
  START("/start"),
  PING("/ping");

  private final String command;
}
