package aviation.bot.service.services.processors.userstates;

import aviation.bot.service.services.processors.PlainTextProcessor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.configurations.MessagesConfig;
import project.vilsoncake.common.entities.AirportEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.models.AirportRequest;
import project.vilsoncake.common.models.AirportResponse;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.models.UserStateResponseTemplate;
import project.vilsoncake.common.repositories.AirportDatabaseProvider;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterDatabaseProvider;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;
import project.vilsoncake.common.services.adapters.FlightradarApiLambdaAdapter;
import project.vilsoncake.common.utils.BotTemplatesResolver;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class ChoosingAirportPlainTextProcessor implements PlainTextProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final AirportDatabaseProvider airportDatabaseProvider;
  private final WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider;
  private final UserAircraftFamilyFilterDatabaseProvider filterDatabaseProvider;
  private final FlightradarApiLambdaAdapter flightradarApiLambdaAdapter;
  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;
  private final MessagesConfig messagesConfig;

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

    String message =
        String.format(
            botTemplatesResolver.getTemplate(
                getUserState(), UserStateResponseTemplate.VALID_AIRPORT_SELECTED),
            airport.getName(),
            airport.getIcao(),
            airport.getIata(),
            airport.getCity(),
            airport.getCountry());

    telegramClient.sendMessage(chatId, message);

    sendAircraftFilterRecommendationIfNeeded(airport, chatId, user);
  }

  private void sendAircraftFilterRecommendationIfNeeded(
      AirportEntity airport, long chatId, UserEntity user) {
    try {
      long selectedAircraftFamilyCount = filterDatabaseProvider.countSelectedFamilies(user);

      if (selectedAircraftFamilyCount > 0) {
        return;
      }

      List<WideBodyAircraftEntity> allWideBodyAircraft =
          widebodyAircraftDatabaseProvider.getAllWideBodyAircraft();
      AirportRequest airportRequest =
          AirportRequest.builder()
              .withAirportCode(airport.getIcao())
              .withAircraftFilterCodes(
                  allWideBodyAircraft.stream().map(WideBodyAircraftEntity::getCode).toList())
              .build();

      Map<String, AirportResponse> airportResponse =
          flightradarApiLambdaAdapter.getFilteredFlightsForAirports(List.of(airportRequest));

      if (airportResponse.containsKey(airport.getIcao())) {
        AirportResponse response = airportResponse.get(airport.getIcao());
        int filteredArrivalsCount = response.getFilteredArrivalsCount();

        if (filteredArrivalsCount > messagesConfig.getAircraftFilterRecommendationThreshold()) {
          String aircraftFilterRecommendationMessage =
              String.format(
                  botTemplatesResolver.getTemplate(
                      getUserState(), UserStateResponseTemplate.AIRCRAFT_FILTER_RECOMMENDATION),
                  BotCommand.AIRCRAFT.getCommand());

          telegramClient.sendMessage(chatId, aircraftFilterRecommendationMessage);
          log.info(
              "Sent aircraft filter recommendation for the message to user {}", user.getUsername());
        }
      }
    } catch (Exception e) {
      log.error("Error while retrieving wide-body flights from flightradar API", e);
    }
  }
}
