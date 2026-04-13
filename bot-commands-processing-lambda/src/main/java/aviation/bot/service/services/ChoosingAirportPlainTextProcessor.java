package aviation.bot.service.services;

import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.AirportEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.repositories.AirportDatabaseProvider;
import project.vilsoncake.common.repositories.UserDatabaseProvider;

import java.util.Optional;

@RequiredArgsConstructor(staticName = "create")
public class ChoosingAirportPlainTextProcessor implements PlainTextProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final AirportDatabaseProvider airportDatabaseProvider;
  private final TelegramClient telegramClient;

  // TODO: replace with actual messages
  private static final String AIRPORT_NOT_FOUND_MESSAGE = "Airport with code %s not found";
  private static final String AIRPORT_CHOSEN_MESSAGE = "Great You have chosen airport %s";

  @Override
  public UserState getUserState() {
    return UserState.CHOOSING_AIRPORT;
  }

  @Override
  public void process(String username, long chatId, String airportCode) {
    Optional<UserEntity> userEntity = userDatabaseProvider.getByUsername(username);

    // Silently ignore unknown users
    if (userEntity.isEmpty()) {
      return;
    }

    String upperCaseAirportCode = airportCode.toUpperCase();

    Optional<AirportEntity> airportByCode = airportDatabaseProvider.getAirportByCode(upperCaseAirportCode);

    if (airportByCode.isEmpty()) {
      telegramClient.sendMessage(chatId, String.format(AIRPORT_NOT_FOUND_MESSAGE, upperCaseAirportCode));
      return;
    }

    UserEntity user = userEntity.get();
    AirportEntity airport = airportByCode.get();

    userDatabaseProvider.updateAirportAndState(user, airport, UserState.ALL_SET);
    telegramClient.sendMessage(chatId, String.format(AIRPORT_CHOSEN_MESSAGE, airport.getIata()));
  }
}
