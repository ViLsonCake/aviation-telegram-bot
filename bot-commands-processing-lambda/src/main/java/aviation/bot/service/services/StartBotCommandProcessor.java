package aviation.bot.service.services;

import aviation.bot.service.models.BotCommand;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.repositories.UserDatabaseProvider;

@RequiredArgsConstructor(staticName = "create")
public class StartBotCommandProcessor implements BotCommandProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final TelegramClient telegramClient;

  private static final String START_MESSAGE = "Welcome to Aviation Bot!\n\nProvide ICAO or IATA code of the airport you want to choose."; // TODO: replace with actual message

  @Override
  public BotCommand getProcessorCommand() {
    return BotCommand.START;
  }

  @Override
  public void process(String username, long chatId, String text) {
    userDatabaseProvider.create(username, chatId);
    telegramClient.sendMessage(chatId, START_MESSAGE);
  }
}
