package aviation.bot.service.configurations;

import aviation.bot.service.handlers.TelegramHandler;
import aviation.bot.service.services.ChoosingAirportPlainTextProcessor;
import aviation.bot.service.services.CommandMessageContentTypeProcessor;
import aviation.bot.service.services.PingBotCommandProcessor;
import aviation.bot.service.services.PlainTextMessageContentTypeProcessor;
import aviation.bot.service.services.StartBotCommandProcessor;
import aviation.bot.service.services.adapters.BotCommandProcessorsAdapter;
import aviation.bot.service.services.adapters.MessageContentTypeAdapter;
import aviation.bot.service.services.adapters.PlainTextProcessorsAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.configurations.BotConfig;
import project.vilsoncake.common.configurations.GeneralConfig;
import project.vilsoncake.common.repositories.AirportDatabaseProvider;
import project.vilsoncake.common.repositories.AirportRepository;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

@Configuration
public class AppBoostrap {

  @Bean
  @ConfigurationProperties(prefix = "bot")
  public BotConfig botConfig() {
    return new BotConfig();
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClient.newHttpClient();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  // Telegram
  @Bean
  public TelegramClient telegramClient(HttpClient httpClient, BotConfig botConfig) {
    return TelegramClient.create(httpClient, botConfig);
  }

  @Bean
  public TelegramHandler telegramHandler(MessageContentTypeAdapter messageContentTypeAdapter, ObjectMapper objectMapper) {
    return TelegramHandler.create(messageContentTypeAdapter, objectMapper);
  }

  // Processors
  @Bean
  public PingBotCommandProcessor pingBotCommandProcessor(TelegramClient telegramClient) {
    return PingBotCommandProcessor.create(telegramClient);
  }

  @Bean
  public StartBotCommandProcessor startBotCommandProcessor(UserDatabaseProvider userDatabaseProvider, TelegramClient telegramClient) {
    return StartBotCommandProcessor.create(userDatabaseProvider, telegramClient);
  }

  @Bean
  public BotCommandProcessorsAdapter botCommandAdapter(PingBotCommandProcessor pingBotCommandProcessor, StartBotCommandProcessor startBotCommandProcessor) {
    BotCommandProcessorsAdapter botCommandProcessorsAdapter = BotCommandProcessorsAdapter.create();

    botCommandProcessorsAdapter.registerCommandProcessor(startBotCommandProcessor);
    botCommandProcessorsAdapter.registerCommandProcessor(pingBotCommandProcessor);

    return botCommandProcessorsAdapter;
  }

  @Bean
  public CommandMessageContentTypeProcessor commandMessageContentTypeProcessor(BotCommandProcessorsAdapter botCommandProcessorsAdapter) {
    return CommandMessageContentTypeProcessor.create(botCommandProcessorsAdapter);
  }

  @Bean
  public ChoosingAirportPlainTextProcessor choosingAirportPlainTextProcessor(UserDatabaseProvider userDatabaseProvider, AirportDatabaseProvider airportDatabaseProvider, TelegramClient telegramClient) {
    return ChoosingAirportPlainTextProcessor.create(userDatabaseProvider, airportDatabaseProvider, telegramClient);
  }

  @Bean
  public PlainTextProcessorsAdapter plainTextProcessorsAdapter(ChoosingAirportPlainTextProcessor choosingAirportPlainTextProcessor) {
    PlainTextProcessorsAdapter plainTextProcessorsAdapter = PlainTextProcessorsAdapter.create();

    plainTextProcessorsAdapter.registerPlainTextProcessor(choosingAirportPlainTextProcessor);

    return plainTextProcessorsAdapter;
  }

  @Bean
  public PlainTextMessageContentTypeProcessor plainTextMessageContentTypeProcessor(UserDatabaseProvider userDatabaseProvider, PlainTextProcessorsAdapter plainTextProcessorsAdapter) {
    return PlainTextMessageContentTypeProcessor.create(userDatabaseProvider, plainTextProcessorsAdapter);
  }

  @Bean
  public MessageContentTypeAdapter messageContentTypeAdapter(CommandMessageContentTypeProcessor commandMessageContentTypeProcessor, PlainTextMessageContentTypeProcessor plainTextMessageContentTypeProcessor) {
    MessageContentTypeAdapter messageContentTypeAdapter = MessageContentTypeAdapter.create();

    messageContentTypeAdapter.registerMessageContentTypeProcessor(commandMessageContentTypeProcessor);
    messageContentTypeAdapter.registerMessageContentTypeProcessor(plainTextMessageContentTypeProcessor);

    return messageContentTypeAdapter;
  }

  @Bean
  @ConfigurationProperties(prefix = "general")
  public GeneralConfig generalConfig() {
    return new GeneralConfig();
  }

  @Bean
  public UserDatabaseProvider userDatabaseProvider(UserRepository userRepository, GeneralConfig generalConfig) {
    return UserDatabaseProvider.create(userRepository, generalConfig);
  }

  @Bean
  public AirportDatabaseProvider airportDatabaseProvider(AirportRepository airportRepository) {
    return AirportDatabaseProvider.create(airportRepository);
  }
}
