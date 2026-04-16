package aviation.bot.service.services.processors.userstates;

import aviation.bot.service.services.processors.PlainTextProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.AirportEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.models.UserStateResponseTemplate;
import project.vilsoncake.common.repositories.AirportDatabaseProvider;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@RequiredArgsConstructor(staticName = "create")
public class ChoosingAirportPlainTextProcessor implements PlainTextProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final AirportDatabaseProvider airportDatabaseProvider;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;

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

    Optional<AirportEntity> airportByCode =
        airportDatabaseProvider.getAirportByCode(upperCaseAirportCode);

    if (airportByCode.isEmpty()) {
      telegramClient.sendMessage(
          chatId,
          String.format(
              botTemplatesResolver.getTemplate(
                  getUserState(), UserStateResponseTemplate.AIRPORT_NOT_FOUND),
              upperCaseAirportCode));
      return;
    }

    UserEntity user = userEntity.get();
    AirportEntity airport = airportByCode.get();

    userDatabaseProvider.updateAirportAndState(user, airport, UserState.ALL_SET);
    telegramClient.sendMessage(
        chatId,
        String.format(
            botTemplatesResolver.getTemplate(
                getUserState(), UserStateResponseTemplate.VALID_AIRPORT_SELECTED),
            airport.getName(),
            airport.getIcao(),
            airport.getIata(),
            airport.getCity(),
            airport.getCountry()));
  }
}
