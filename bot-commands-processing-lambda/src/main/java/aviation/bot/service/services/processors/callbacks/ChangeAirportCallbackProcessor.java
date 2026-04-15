package aviation.bot.service.services.processors.callbacks;

import aviation.bot.service.services.processors.CallbackProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class ChangeAirportCallbackProcessor implements CallbackProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  @Override
  public CallbackType getCallbackType() {
    return CallbackType.CHANGE_AIRPORT;
  }

  @Override
  public void process(String username, long chatId, String callbackId) {
    telegramClient.answerCallbackQuery(callbackId);

    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      return;
    }

    UserEntity user = optionalUser.get();

    userDatabaseProvider.updateState(user, UserState.CHOOSING_AIRPORT);

    telegramClient.sendMessage(
        chatId, botTemplatesResolver.getTemplate(CallbackType.CHANGE_AIRPORT).getMessageTemplate());
  }
}
