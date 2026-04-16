package aviation.bot.service.services.processors.callbacks;

import aviation.bot.service.services.processors.CallbackProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.clients.telegram.InlineKeyboardButton;
import project.vilsoncake.common.clients.telegram.InlineKeyboardMarkup;
import project.vilsoncake.common.entities.UserEntity;
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
  public void process(String username, long chatId, String callbackId) {
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
    // Default mode
    CallbackType defaultModeCallbackType = CallbackType.DEFAULT_BOT_MODE_SELECTED;
    String defaultModeButtonText =
        botTemplatesResolver.getTemplate(defaultModeCallbackType).getButtonText();

    // Only scheduled flights mode
    CallbackType onlyScheduledFlightsModeCallbackType =
        CallbackType.ONLY_SCHEDULED_FLIGHTS_BOT_MODE_SELECTED;
    String onlyScheduledModeButtonText =
        botTemplatesResolver.getTemplate(onlyScheduledFlightsModeCallbackType).getButtonText();

    // Only specific aircraft mode
    CallbackType onlySpecificAircraftModeCallbackType =
        CallbackType.ONLY_SPECIFIC_AIRCRAFT_BOT_MODE_SELECTED;
    String onlySpecificAircraftModeButtonText =
        botTemplatesResolver.getTemplate(onlySpecificAircraftModeCallbackType).getButtonText();

    // Mute mode
    CallbackType muteModeCallbackType = CallbackType.MUTE_BOT_MODE_SELECTED;
    String muteModeButtonText =
        botTemplatesResolver.getTemplate(muteModeCallbackType).getButtonText();

    return InlineKeyboardMarkup.builder()
        .addRow(
            InlineKeyboardButton.of(
                onlyScheduledModeButtonText, onlyScheduledFlightsModeCallbackType.name()))
        .addRow(
            InlineKeyboardButton.of(
                onlySpecificAircraftModeButtonText, onlySpecificAircraftModeCallbackType.name()))
        .addRow(
            InlineKeyboardButton.of(defaultModeButtonText, defaultModeCallbackType.name()),
            InlineKeyboardButton.of(muteModeButtonText, muteModeCallbackType.name()))
        .build();
  }
}
