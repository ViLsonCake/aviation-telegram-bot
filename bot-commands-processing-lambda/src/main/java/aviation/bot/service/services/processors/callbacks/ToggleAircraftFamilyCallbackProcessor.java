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
public class ToggleAircraftFamilyCallbackProcessor implements CallbackProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final AircraftFamilyFilterService aircraftFamilyFilterService;
  private final TelegramClient telegramClient;

  @Override
  public CallbackType getCallbackType() {
    return CallbackType.TOGGLE_AIRCRAFT_FAMILY;
  }

  @Override
  public void process(String username, long chatId, String callbackData, String callbackId) {
    Optional<UserEntity> optionalUser = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (optionalUser.isEmpty()) {
      telegramClient.answerCallbackQuery(callbackId);
      return;
    }

    UserEntity user = optionalUser.get();
    String familyCode = callbackData.split("\\|", 2)[1];

    aircraftFamilyFilterService.toggleFamilyAndSendKeyboard(chatId, user, familyCode, callbackId);
  }
}
