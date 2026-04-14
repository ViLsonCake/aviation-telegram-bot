package aviation.bot.service.services.processors.commands;

import aviation.bot.service.services.processors.BotCommandProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class StartBotCommandProcessor implements BotCommandProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

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
    telegramClient.sendMessage(chatId, botTemplatesResolver.getTemplate(getProcessorCommand()));
  }
}
