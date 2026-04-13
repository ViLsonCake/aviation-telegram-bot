package aviation.bot.service.services;

import aviation.bot.service.models.BotCommand;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.repositories.UserDatabaseProvider;

import java.util.Optional;

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
    Optional<UserEntity> user = userDatabaseProvider.getByUsername(username);
    if (user.isEmpty()) {
      userDatabaseProvider.create(username, chatId);
    } else {
      userDatabaseProvider.updateState(user.get(), UserState.CHOOSING_AIRPORT);
    }
    telegramClient.sendMessage(chatId, START_MESSAGE);
  }
}
