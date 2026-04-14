package aviation.bot.service.services.processors.commands;

import aviation.bot.service.services.processors.BotCommandProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.AirportEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class AirportBotCommandProcessor implements BotCommandProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  @Override
  public BotCommand getProcessorCommand() {
    return BotCommand.AIRPORT;
  }

  @Override
  public void process(String username, long chatId, String text) {
    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      return;
    }

    UserEntity user = optionalUser.get();
    AirportEntity airport = user.getAirport();

    // Silently ignore users that don't have airport set
    if (airport == null) {
      return;
    }

    String messageTemplate = botTemplatesResolver.getTemplate(BotCommand.AIRPORT);
    String message =
        String.format(messageTemplate, airport.getName(), airport.getIcao(), airport.getIata());

    telegramClient.sendMessage(chatId, message);
  }
}
