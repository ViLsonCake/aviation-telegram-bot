package aviation.bot.service.configurations;

import aviation.bot.service.handlers.TelegramHandler;
import aviation.bot.service.services.adapters.BotCommandProcessorsAdapter;
import aviation.bot.service.services.adapters.CallbackProcessorsAdapter;
import aviation.bot.service.services.adapters.MessageContentTypeAdapter;
import aviation.bot.service.services.adapters.PlainTextProcessorsAdapter;
import aviation.bot.service.services.processors.callbacks.ChangeAirportCallbackProcessor;
import aviation.bot.service.services.processors.commands.AirportBotCommandProcessor;
import aviation.bot.service.services.processors.commands.PingBotCommandProcessor;
import aviation.bot.service.services.processors.commands.StartBotCommandProcessor;
import aviation.bot.service.services.processors.contenttypes.CallbackMessageContentTypeProcessor;
import aviation.bot.service.services.processors.contenttypes.CommandMessageContentTypeProcessor;
import aviation.bot.service.services.processors.contenttypes.PlainTextMessageContentTypeProcessor;
import aviation.bot.service.services.processors.userstates.ChoosingAirportPlainTextProcessor;
import java.net.http.HttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.configurations.BotConfig;
import project.vilsoncake.common.configurations.GeneralConfig;
import project.vilsoncake.common.messages.BotTemplates;
import project.vilsoncake.common.repositories.AirportDatabaseProvider;
import project.vilsoncake.common.repositories.AirportRepository;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.UserRepository;
import project.vilsoncake.common.utils.BotTemplatesResolver;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppBoostrap {

  // Telegram Bot
  @Bean
  @ConfigurationProperties(prefix = "bot")
  public BotConfig botConfig() {
    return new BotConfig();
  }

  @Bean
  public TelegramClient telegramClient(
      HttpClient httpClient, BotConfig botConfig, ObjectMapper objectMapper) {
    return TelegramClient.create(httpClient, botConfig, objectMapper);
  }

  @Bean
  public TelegramHandler telegramHandler(
      MessageContentTypeAdapter messageContentTypeAdapter, ObjectMapper objectMapper) {
    return TelegramHandler.create(messageContentTypeAdapter, objectMapper);
  }

  @Bean
  @ConfigurationProperties(prefix = "templates")
  public BotTemplates botTemplates() {
    return new BotTemplates();
  }

  @Bean
  public BotTemplatesResolver botTemplatesResolver(BotTemplates botTemplates) {
    return BotTemplatesResolver.create(botTemplates);
  }

  // Processors - Commands
  @Bean
  public PingBotCommandProcessor pingBotCommandProcessor(
      TelegramClient telegramClient, BotTemplatesResolver botTemplatesResolver) {
    return PingBotCommandProcessor.create(telegramClient, botTemplatesResolver);
  }

  @Bean
  public StartBotCommandProcessor startBotCommandProcessor(
      UserDatabaseProvider userDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return StartBotCommandProcessor.create(
        userDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  @Bean
  public AirportBotCommandProcessor airportBotCommandProcessor(
      UserDatabaseProvider userDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return AirportBotCommandProcessor.create(
        userDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  // Processors - Content Types
  @Bean
  public CommandMessageContentTypeProcessor commandMessageContentTypeProcessor(
      BotCommandProcessorsAdapter botCommandProcessorsAdapter) {
    return CommandMessageContentTypeProcessor.create(botCommandProcessorsAdapter);
  }

  @Bean
  public PlainTextMessageContentTypeProcessor plainTextMessageContentTypeProcessor(
      UserDatabaseProvider userDatabaseProvider,
      PlainTextProcessorsAdapter plainTextProcessorsAdapter) {
    return PlainTextMessageContentTypeProcessor.create(
        userDatabaseProvider, plainTextProcessorsAdapter);
  }

  @Bean
  public CallbackMessageContentTypeProcessor callbackMessageContentTypeProcessor(
      CallbackProcessorsAdapter callbackProcessorsAdapter) {
    return CallbackMessageContentTypeProcessor.create(callbackProcessorsAdapter);
  }

  // Processors - User States
  @Bean
  public ChoosingAirportPlainTextProcessor choosingAirportPlainTextProcessor(
      UserDatabaseProvider userDatabaseProvider,
      AirportDatabaseProvider airportDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return ChoosingAirportPlainTextProcessor.create(
        userDatabaseProvider, airportDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  // Processors - Callbacks
  @Bean
  public ChangeAirportCallbackProcessor changeAirportCallbackProcessor(
      UserDatabaseProvider userDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return ChangeAirportCallbackProcessor.create(
        userDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  // Adapters
  @Bean
  public BotCommandProcessorsAdapter botCommandAdapter(
      PingBotCommandProcessor pingBotCommandProcessor,
      StartBotCommandProcessor startBotCommandProcessor,
      AirportBotCommandProcessor airportBotCommandProcessor) {
    BotCommandProcessorsAdapter botCommandProcessorsAdapter = BotCommandProcessorsAdapter.create();

    botCommandProcessorsAdapter.registerCommandProcessor(startBotCommandProcessor);
    botCommandProcessorsAdapter.registerCommandProcessor(pingBotCommandProcessor);
    botCommandProcessorsAdapter.registerCommandProcessor(airportBotCommandProcessor);

    return botCommandProcessorsAdapter;
  }

  @Bean
  public MessageContentTypeAdapter messageContentTypeAdapter(
      CommandMessageContentTypeProcessor commandMessageContentTypeProcessor,
      PlainTextMessageContentTypeProcessor plainTextMessageContentTypeProcessor,
      CallbackMessageContentTypeProcessor callbackMessageContentTypeProcessor) {
    MessageContentTypeAdapter messageContentTypeAdapter = MessageContentTypeAdapter.create();

    messageContentTypeAdapter.registerMessageContentTypeProcessor(
        commandMessageContentTypeProcessor);
    messageContentTypeAdapter.registerMessageContentTypeProcessor(
        plainTextMessageContentTypeProcessor);
    messageContentTypeAdapter.registerMessageContentTypeProcessor(
        callbackMessageContentTypeProcessor);

    return messageContentTypeAdapter;
  }

  @Bean
  public PlainTextProcessorsAdapter plainTextProcessorsAdapter(
      ChoosingAirportPlainTextProcessor choosingAirportPlainTextProcessor) {
    PlainTextProcessorsAdapter plainTextProcessorsAdapter = PlainTextProcessorsAdapter.create();

    plainTextProcessorsAdapter.registerPlainTextProcessor(choosingAirportPlainTextProcessor);

    return plainTextProcessorsAdapter;
  }

  @Bean
  public CallbackProcessorsAdapter callbackProcessorsAdapter(
      ChangeAirportCallbackProcessor changeAirportCallbackProcessor) {
    CallbackProcessorsAdapter callbackProcessorsAdapter = CallbackProcessorsAdapter.create();

    callbackProcessorsAdapter.registerCallbackProcessor(changeAirportCallbackProcessor);

    return callbackProcessorsAdapter;
  }

  // Database
  @Bean
  public UserDatabaseProvider userDatabaseProvider(
      UserRepository userRepository, GeneralConfig generalConfig) {
    return UserDatabaseProvider.create(userRepository, generalConfig);
  }

  @Bean
  public AirportDatabaseProvider airportDatabaseProvider(AirportRepository airportRepository) {
    return AirportDatabaseProvider.create(airportRepository);
  }

  // Other required beans
  @Bean
  @ConfigurationProperties(prefix = "general")
  public GeneralConfig generalConfig() {
    return new GeneralConfig();
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClient.newHttpClient();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
