package aviation.bot.service.services.processors.callbacks;

import aviation.bot.service.services.processors.CallbackProcessor;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.clients.telegram.InlineKeyboardButton;
import project.vilsoncake.common.clients.telegram.InlineKeyboardMarkup;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.BotMode;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class ChangeBotModeCallbackProcessor implements CallbackProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  @Override
  public CallbackType getCallbackType() {
    return CallbackType.CHANGE_BOT_MODE;
  }

  @Override
  public void process(String username, long chatId, String callbackData, String callbackId) {
    telegramClient.answerCallbackQuery(callbackId);

    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      return;
    }

    UserEntity user = optionalUser.get();
    userDatabaseProvider.updateState(user, UserState.CHOOSING_BOT_MODE);
    telegramClient.sendMessage(
        chatId,
        botTemplatesResolver.getTemplate(getCallbackType()).getMessageTemplate(),
        buildInlineKeyboardMarkupToSelectBotMode());
  }

  private InlineKeyboardMarkup buildInlineKeyboardMarkupToSelectBotMode() {
    Map<String, String> buttons = botTemplatesResolver.getTemplate(getCallbackType()).getButtons();

    return InlineKeyboardMarkup.builder()
        .addRow(
            InlineKeyboardButton.of(
                buttons.get(BotMode.ONLY_SCHEDULED_FLIGHTS.name()),
                CallbackType.CHANGE_MODE.name() + "|" + BotMode.ONLY_SCHEDULED_FLIGHTS.name()))
        .addRow(
            InlineKeyboardButton.of(
                buttons.get(BotMode.ONLY_SPECIFIC_AIRCRAFT.name()),
                CallbackType.CHANGE_MODE.name() + "|" + BotMode.ONLY_SPECIFIC_AIRCRAFT.name()))
        .addRow(
            InlineKeyboardButton.of(
                buttons.get(BotMode.DEFAULT.name()),
                CallbackType.CHANGE_MODE.name() + "|" + BotMode.DEFAULT.name()),
            InlineKeyboardButton.of(
                buttons.get(BotMode.MUTE.name()),
                CallbackType.CHANGE_MODE.name() + "|" + BotMode.MUTE.name()))
        .build();
  }
}
