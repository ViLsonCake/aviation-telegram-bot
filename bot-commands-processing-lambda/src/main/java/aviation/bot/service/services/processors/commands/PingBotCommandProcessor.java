package aviation.bot.service.services.processors.commands;

import aviation.bot.service.services.processors.BotCommandProcessor;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.utils.BotTemplatesResolver;

/** Processor for the /ping command to check bot availability. */
@RequiredArgsConstructor(staticName = "create")
public class PingBotCommandProcessor implements BotCommandProcessor {

  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  @Override
  public BotCommand getProcessorCommand() {
    return BotCommand.PING;
  }

  @Override
  public void process(String username, long chatId, String text) {
    telegramClient.sendMessage(chatId, botTemplatesResolver.getTemplate(getProcessorCommand()));
  }
}
