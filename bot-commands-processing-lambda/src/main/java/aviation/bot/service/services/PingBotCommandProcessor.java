package aviation.bot.service.services;

import aviation.bot.service.models.BotCommand;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;

/** Processor for the /ping command to check bot availability. */
@RequiredArgsConstructor(staticName = "create")
public class PingBotCommandProcessor implements BotCommandProcessor {

  private final TelegramClient telegramClient;

  private static final String PING_MESSAGE = "pong";

  @Override
  public BotCommand getProcessorCommand() {
    return BotCommand.PING;
  }

  @Override
  public void process(long chatId, String text) {
    telegramClient.sendMessage(chatId, PING_MESSAGE);
  }
}
