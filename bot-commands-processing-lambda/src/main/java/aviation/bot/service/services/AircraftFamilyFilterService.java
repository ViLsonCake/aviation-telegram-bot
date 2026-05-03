package aviation.bot.service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.clients.telegram.InlineKeyboardButton;
import project.vilsoncake.common.clients.telegram.InlineKeyboardMarkup;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.models.AircraftFamily;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class AircraftFamilyFilterService {

  private static final int MAX_FAMILIES = 10;
  private static final int BUTTONS_PER_ROW = 3;

  private final UserAircraftFamilyFilterDatabaseProvider filterDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

  public void sendFilterKeyboard(long chatId, UserEntity user) {
    Set<String> selectedFamilies = filterDatabaseProvider.getFilterFamilyCodes(user);
    boolean isDefaultAllSelected = selectedFamilies.isEmpty();

    String message =
        botTemplatesResolver.getTemplate(CallbackType.AIRCRAFT_FILTER).getMessageTemplate();
    InlineKeyboardMarkup keyboard = buildFilterKeyboard(selectedFamilies, isDefaultAllSelected);

    telegramClient.sendMessage(chatId, message, keyboard);
  }

  public void toggleFamilyAndSendKeyboard(
      long chatId, UserEntity user, String familyCode, String callbackId) {
    boolean isCurrentlySelected = filterDatabaseProvider.isFamilySelected(user, familyCode);

    if (isCurrentlySelected) {
      filterDatabaseProvider.removeFamily(user, familyCode);
      long remaining = filterDatabaseProvider.countSelectedFamilies(user);
      if (remaining == 0) {
        filterDatabaseProvider.clearFilter(user);
      }
      telegramClient.answerCallbackQuery(callbackId);
    } else {
      long currentCount = filterDatabaseProvider.countSelectedFamilies(user);
      if (currentCount >= MAX_FAMILIES) {
        telegramClient.answerCallbackQueryWithAlert(
            callbackId, "You can select up to " + MAX_FAMILIES + " aircraft families.");
        return;
      }
      filterDatabaseProvider.addFamily(user, familyCode);
      telegramClient.answerCallbackQuery(callbackId);
    }

    sendFilterKeyboard(chatId, user);
  }

  public void resetFilterAndSendKeyboard(long chatId, UserEntity user) {
    filterDatabaseProvider.clearFilter(user);
    sendFilterKeyboard(chatId, user);
  }

  private InlineKeyboardMarkup buildFilterKeyboard(
      Set<String> selectedFamilies, boolean isDefaultAllSelected) {
    AircraftFamily[] families = AircraftFamily.values();
    InlineKeyboardMarkup.Builder builder = InlineKeyboardMarkup.builder();

    List<InlineKeyboardButton> rowBuffer = new ArrayList<>();

    for (AircraftFamily family : families) {
      boolean selected = isDefaultAllSelected || selectedFamilies.contains(family.name());
      String emoji = selected ? "✅" : "☐";
      String buttonText = emoji + " " + family.getDisplayName();
      String callbackData = CallbackType.TOGGLE_AIRCRAFT_FAMILY.name() + "|" + family.name();

      rowBuffer.add(InlineKeyboardButton.of(buttonText, callbackData));

      if (rowBuffer.size() == BUTTONS_PER_ROW) {
        builder.addRow(rowBuffer.toArray(new InlineKeyboardButton[0]));
        rowBuffer.clear();
      }
    }

    if (!rowBuffer.isEmpty()) {
      builder.addRow(rowBuffer.toArray(new InlineKeyboardButton[0]));
    }

    if (!isDefaultAllSelected) {
      String resetButtonText =
          botTemplatesResolver.getTemplate(CallbackType.RESET_AIRCRAFT_FILTER).getButtonText();
      builder.addRow(
          InlineKeyboardButton.of(resetButtonText, CallbackType.RESET_AIRCRAFT_FILTER.name()));
    }

    return builder.build();
  }
}
