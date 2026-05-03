package aviation.bot.service.services.processors.callbacks;

import aviation.bot.service.services.AircraftFamilyFilterService;
import aviation.bot.service.services.processors.CallbackProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.repositories.UserDatabaseProvider;

@RequiredArgsConstructor(staticName = "create")
public class ResetAircraftFilterCallbackProcessor implements CallbackProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final AircraftFamilyFilterService aircraftFamilyFilterService;
  private final TelegramClient telegramClient;

  @Override
  public CallbackType getCallbackType() {
    return CallbackType.RESET_AIRCRAFT_FILTER;
  }

  @Override
  public void process(String username, long chatId, String callbackData, String callbackId) {
    telegramClient.answerCallbackQuery(callbackId);

    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      return;
    }

    aircraftFamilyFilterService.resetFilterAndSendKeyboard(chatId, optionalUser.get());
  }
}
