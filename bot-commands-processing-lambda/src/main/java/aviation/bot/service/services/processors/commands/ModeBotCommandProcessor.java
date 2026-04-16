package aviation.bot.service.services.processors.commands;

import aviation.bot.service.services.processors.BotCommandProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.clients.telegram.InlineKeyboardButton;
import project.vilsoncake.common.clients.telegram.InlineKeyboardMarkup;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.BotMode;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class ModeBotCommandProcessor implements BotCommandProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  @Override
  public BotCommand getProcessorCommand() {
    return BotCommand.MODE;
  }

  @Override
  public void process(String username, long chatId, String text) {
    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      return;
    }

    UserEntity user = optionalUser.get();
    BotMode botMode = user.getBotMode();
    String messageTemplate = botTemplatesResolver.getTemplate(getProcessorCommand());
    String message = String.format(messageTemplate, botMode.name());

    telegramClient.sendMessage(chatId, message, buildInlineKeyboardMarkupToChangeBotMode());
  }

  private InlineKeyboardMarkup buildInlineKeyboardMarkupToChangeBotMode() {
    CallbackType callbackType = CallbackType.CHANGE_BOT_MODE;
    String inlineButtonText = botTemplatesResolver.getTemplate(callbackType).getButtonText();
    String callbackData = callbackType.name();
    return InlineKeyboardMarkup.builder()
        .addRow(InlineKeyboardButton.of(inlineButtonText, callbackData))
        .build();
  }
}
