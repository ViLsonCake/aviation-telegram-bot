package aviation.bot.service.services;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.BotMode;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class BotModeChangeService {
  private final UserDatabaseProvider userDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  private final Map<CallbackType, BotMode> botModeByCallbackType =
      Map.of(
          CallbackType.DEFAULT_BOT_MODE_SELECTED, BotMode.DEFAULT,
          CallbackType.ONLY_SCHEDULED_FLIGHTS_BOT_MODE_SELECTED, BotMode.ONLY_SCHEDULED_FLIGHTS,
          CallbackType.ONLY_SPECIFIC_AIRCRAFT_BOT_MODE_SELECTED, BotMode.ONLY_SPECIFIC_AIRCRAFT,
          CallbackType.MUTE_BOT_MODE_SELECTED, BotMode.MUTE);

  public void handleCallback(
      CallbackType callbackType, String username, long chatId, String callbackId) {
    telegramClient.answerCallbackQuery(callbackId);

    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      return;
    }

    UserEntity user = optionalUser.get();
    BotMode botMode = getBotModeByCallbackType(callbackType);
    userDatabaseProvider.updateBotModeAnsState(user, botMode, UserState.ALL_SET);

    telegramClient.sendMessage(
        chatId, botTemplatesResolver.getTemplate(callbackType).getMessageTemplate());
  }

  private BotMode getBotModeByCallbackType(CallbackType callbackType) {
    return botModeByCallbackType.get(callbackType);
  }
}
