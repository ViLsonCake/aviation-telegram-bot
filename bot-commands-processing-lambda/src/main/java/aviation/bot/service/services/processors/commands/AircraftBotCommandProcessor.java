package aviation.bot.service.services.processors.commands;

import aviation.bot.service.services.processors.BotCommandProcessor;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.clients.telegram.InlineKeyboardButton;
import project.vilsoncake.common.clients.telegram.InlineKeyboardMarkup;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.models.AircraftFamily;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterDatabaseProvider;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class AircraftBotCommandProcessor implements BotCommandProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final UserAircraftFamilyFilterDatabaseProvider filterDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  @Override
  public BotCommand getProcessorCommand() {
    return BotCommand.AIRCRAFT;
  }

  @Override
  public void process(String username, long chatId, String text) {
    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      return;
    }

    UserEntity user = optionalUser.get();
    Set<String> selectedFamilyCodes = filterDatabaseProvider.getFilterFamilyCodes(user);
    String filterStatus = buildFilterStatusText(selectedFamilyCodes);
    String messageTemplate = botTemplatesResolver.getTemplate(getProcessorCommand());
    String message = String.format(messageTemplate, filterStatus);

    String manageButtonText =
        botTemplatesResolver.getTemplate(CallbackType.AIRCRAFT_FILTER).getButtonText();
    InlineKeyboardMarkup keyboard =
        InlineKeyboardMarkup.builder()
            .addRow(InlineKeyboardButton.of(manageButtonText, CallbackType.AIRCRAFT_FILTER.name()))
            .build();

    telegramClient.sendMessage(chatId, message, keyboard);
  }

  private String buildFilterStatusText(Set<String> selectedFamilyCodes) {
    if (selectedFamilyCodes.isEmpty()) {
      return "All aircraft (default)";
    }

    return selectedFamilyCodes.stream()
        .map(
            code -> {
              try {
                return AircraftFamily.valueOf(code).getDisplayName();
              } catch (IllegalArgumentException e) {
                return code;
              }
            })
        .sorted()
        .collect(Collectors.joining(", "));
  }
}
