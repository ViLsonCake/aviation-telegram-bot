package aviation.bot.service.configurations;

import aviation.bot.service.handlers.TelegramHandler;
import aviation.bot.service.services.AircraftFamilyFilterService;
import aviation.bot.service.services.adapters.BotCommandProcessorsAdapter;
import aviation.bot.service.services.adapters.CallbackProcessorsAdapter;
import aviation.bot.service.services.adapters.MessageContentTypeAdapter;
import aviation.bot.service.services.adapters.PlainTextProcessorsAdapter;
import aviation.bot.service.services.processors.BotCommandProcessor;
import aviation.bot.service.services.processors.CallbackProcessor;
import aviation.bot.service.services.processors.MessageContentTypeProcessor;
import aviation.bot.service.services.processors.PlainTextProcessor;
import aviation.bot.service.services.processors.callbacks.AircraftFilterCallbackProcessor;
import aviation.bot.service.services.processors.callbacks.ChangeAirportCallbackProcessor;
import aviation.bot.service.services.processors.callbacks.ChangeBotModeCallbackProcessor;
import aviation.bot.service.services.processors.callbacks.ChangeModeCallbackProcessor;
import aviation.bot.service.services.processors.callbacks.ResetAircraftFilterCallbackProcessor;
import aviation.bot.service.services.processors.callbacks.ToggleAircraftFamilyCallbackProcessor;
import aviation.bot.service.services.processors.commands.AircraftBotCommandProcessor;
import aviation.bot.service.services.processors.commands.AirportBotCommandProcessor;
import aviation.bot.service.services.processors.commands.ModeBotCommandProcessor;
import aviation.bot.service.services.processors.commands.PingBotCommandProcessor;
import aviation.bot.service.services.processors.commands.StartBotCommandProcessor;
import aviation.bot.service.services.processors.contenttypes.CallbackMessageContentTypeProcessor;
import aviation.bot.service.services.processors.contenttypes.CommandMessageContentTypeProcessor;
import aviation.bot.service.services.processors.contenttypes.PlainTextMessageContentTypeProcessor;
import aviation.bot.service.services.processors.userstates.ChoosingAirportPlainTextProcessor;
import java.net.http.HttpClient;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.configurations.AwsConfig;
import project.vilsoncake.common.configurations.BotConfig;
import project.vilsoncake.common.configurations.GeneralConfig;
import project.vilsoncake.common.configurations.MessagesConfig;
import project.vilsoncake.common.messages.BotTemplates;
import project.vilsoncake.common.repositories.AircraftFamilyRepository;
import project.vilsoncake.common.repositories.AirportDatabaseProvider;
import project.vilsoncake.common.repositories.AirportRepository;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterDatabaseProvider;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterRepository;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.UserRepository;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;
import project.vilsoncake.common.repositories.WidebodyAircraftRepository;
import project.vilsoncake.common.services.adapters.FlightradarApiLambdaAdapter;
import project.vilsoncake.common.utils.BotTemplatesResolver;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfiguration {

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

  @Bean
  public ModeBotCommandProcessor modeBotCommandProcessor(
      UserDatabaseProvider userDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return ModeBotCommandProcessor.create(
        userDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  @Bean
  public AircraftBotCommandProcessor aircraftBotCommandProcessor(
      UserDatabaseProvider userDatabaseProvider,
      UserAircraftFamilyFilterDatabaseProvider filterDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return AircraftBotCommandProcessor.create(
        userDatabaseProvider, filterDatabaseProvider, telegramClient, botTemplatesResolver);
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
      BotTemplatesResolver botTemplatesResolver,
      FlightradarApiLambdaAdapter flightradarApiLambdaAdapter,
      MessagesConfig messagesConfig,
      WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider,
      UserAircraftFamilyFilterDatabaseProvider userAircraftFamilyFilterDatabaseProvider) {
    return ChoosingAirportPlainTextProcessor.create(
        userDatabaseProvider,
        airportDatabaseProvider,
        widebodyAircraftDatabaseProvider,
        userAircraftFamilyFilterDatabaseProvider,
        flightradarApiLambdaAdapter,
        telegramClient,
        botTemplatesResolver,
        messagesConfig);
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

  @Bean
  public ChangeBotModeCallbackProcessor changeBotModeCallbackProcessor(
      UserDatabaseProvider userDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return ChangeBotModeCallbackProcessor.create(
        userDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  @Bean
  public ChangeModeCallbackProcessor changeModeCallbackProcessor(
      UserDatabaseProvider userDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return ChangeModeCallbackProcessor.create(
        userDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  @Bean
  public AircraftFilterCallbackProcessor aircraftFilterCallbackProcessor(
      UserDatabaseProvider userDatabaseProvider,
      AircraftFamilyFilterService aircraftFamilyFilterService,
      TelegramClient telegramClient) {
    return AircraftFilterCallbackProcessor.create(
        userDatabaseProvider, aircraftFamilyFilterService, telegramClient);
  }

  @Bean
  public ToggleAircraftFamilyCallbackProcessor toggleAircraftFamilyCallbackProcessor(
      UserDatabaseProvider userDatabaseProvider,
      AircraftFamilyFilterService aircraftFamilyFilterService,
      TelegramClient telegramClient) {
    return ToggleAircraftFamilyCallbackProcessor.create(
        userDatabaseProvider, aircraftFamilyFilterService, telegramClient);
  }

  @Bean
  public ResetAircraftFilterCallbackProcessor resetAircraftFilterCallbackProcessor(
      UserDatabaseProvider userDatabaseProvider,
      AircraftFamilyFilterService aircraftFamilyFilterService,
      TelegramClient telegramClient) {
    return ResetAircraftFilterCallbackProcessor.create(
        userDatabaseProvider, aircraftFamilyFilterService, telegramClient);
  }

  // Services
  @Bean
  public AircraftFamilyFilterService aircraftFamilyFilterService(
      UserAircraftFamilyFilterDatabaseProvider filterDatabaseProvider,
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver) {
    return AircraftFamilyFilterService.create(
        filterDatabaseProvider, telegramClient, botTemplatesResolver);
  }

  // Adapters
  @Bean
  public BotCommandProcessorsAdapter botCommandAdapter(
      List<BotCommandProcessor> botCommandProcessors) {
    return BotCommandProcessorsAdapter.create(botCommandProcessors);
  }

  @Bean
  public MessageContentTypeAdapter messageContentTypeAdapter(
      List<MessageContentTypeProcessor> messageContentTypeProcessors) {
    return MessageContentTypeAdapter.create(messageContentTypeProcessors);
  }

  @Bean
  public PlainTextProcessorsAdapter plainTextProcessorsAdapter(
      List<PlainTextProcessor> plainTextProcessors) {
    return PlainTextProcessorsAdapter.create(plainTextProcessors);
  }

  @Bean
  public CallbackProcessorsAdapter callbackProcessorsAdapter(
      List<CallbackProcessor> callbackProcessors) {
    return CallbackProcessorsAdapter.create(callbackProcessors);
  }

  @Bean
  public FlightradarApiLambdaAdapter flightradarApiLambdaAdapter(
      LambdaClient lambdaClient, AwsConfig awsConfig, ObjectMapper objectMapper) {
    return FlightradarApiLambdaAdapter.create(lambdaClient, awsConfig, objectMapper);
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

  @Bean
  public UserAircraftFamilyFilterDatabaseProvider userAircraftFamilyFilterDatabaseProvider(
      UserAircraftFamilyFilterRepository filterRepository,
      AircraftFamilyRepository aircraftFamilyRepository) {
    return UserAircraftFamilyFilterDatabaseProvider.create(
        filterRepository, aircraftFamilyRepository);
  }

  @Bean
  public WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider(
      WidebodyAircraftRepository widebodyAircraftRepository) {
    return WidebodyAircraftDatabaseProvider.create(widebodyAircraftRepository);
  }

  // AWS
  @Bean
  @ConfigurationProperties(prefix = "aws")
  public AwsConfig awsConfig() {
    return new AwsConfig();
  }

  @Bean
  public LambdaClient lambdaClient(AwsConfig awsConfig) {
    return LambdaClient.builder().region(Region.of(awsConfig.getRegion())).build();
  }

  // Other required beans
  @Bean
  @ConfigurationProperties(prefix = "general")
  public GeneralConfig generalConfig() {
    return new GeneralConfig();
  }

  @Bean
  @ConfigurationProperties(prefix = "messages")
  public MessagesConfig messagesConfig() {
    return new MessagesConfig();
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
