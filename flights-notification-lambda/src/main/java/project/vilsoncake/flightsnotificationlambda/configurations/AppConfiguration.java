package project.vilsoncake.flightsnotificationlambda.configurations;

import java.net.http.HttpClient;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.configurations.BotConfig;
import project.vilsoncake.common.configurations.GeneralConfig;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.UserRepository;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;
import project.vilsoncake.common.repositories.WidebodyAircraftRepository;
import project.vilsoncake.flightsnotificationlambda.handlers.FlightsNotificationTriggerHandler;
import project.vilsoncake.flightsnotificationlambda.models.BotTemplates;
import project.vilsoncake.flightsnotificationlambda.processors.NotificationTypeProcessor;
import project.vilsoncake.flightsnotificationlambda.processors.ScheduledFlightsNotificationTypeProcessor;
import project.vilsoncake.flightsnotificationlambda.services.AircraftNameResolver;
import project.vilsoncake.flightsnotificationlambda.services.BotTemplatesResolver;
import project.vilsoncake.flightsnotificationlambda.services.UserNotificationsService;
import project.vilsoncake.flightsnotificationlambda.services.adapters.FlightradarApiLambdaAdapter;
import project.vilsoncake.flightsnotificationlambda.services.adapters.NotificationTypeAdapter;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfiguration {

  // Telegram
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
  @ConfigurationProperties(prefix = "templates")
  public BotTemplates botTemplates() {
    return new BotTemplates();
  }

  @Bean
  public BotTemplatesResolver botTemplatesResolver(BotTemplates botTemplates) {
    return BotTemplatesResolver.create(botTemplates);
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

  // Processors
  @Bean
  public ScheduledFlightsNotificationTypeProcessor scheduledFlightsNotificationTypeProcessor(
      UserDatabaseProvider userDatabaseProvider,
      WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider,
      FlightradarApiLambdaAdapter flightradarApiLambdaAdapter,
      UserNotificationsService userNotificationsService) {
    return ScheduledFlightsNotificationTypeProcessor.create(
        userDatabaseProvider,
        widebodyAircraftDatabaseProvider,
        flightradarApiLambdaAdapter,
        userNotificationsService);
  }

  // Adapters
  @Bean
  public NotificationTypeAdapter notificationTypeAdapter(
      List<NotificationTypeProcessor> notificationTypeProcessors) {
    return NotificationTypeAdapter.create(notificationTypeProcessors);
  }

  @Bean
  public FlightradarApiLambdaAdapter flightradarApiLambdaAdapter(
      LambdaClient lambdaClient, AwsConfig awsConfig, ObjectMapper objectMapper) {
    return FlightradarApiLambdaAdapter.create(lambdaClient, awsConfig, objectMapper);
  }

  // Handlers
  @Bean
  public FlightsNotificationTriggerHandler flightsNotificationTriggerHandler(
      NotificationTypeAdapter notificationTypeAdapter) {
    return FlightsNotificationTriggerHandler.create(notificationTypeAdapter);
  }

  // Services
  @Bean
  public UserNotificationsService notificationsService(
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver,
      AircraftNameResolver aircraftNameResolver) {
    return UserNotificationsService.create(
        telegramClient, botTemplatesResolver, aircraftNameResolver);
  }

  @Bean
  public AircraftNameResolver aircraftNameResolver(
      WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider) {
    return AircraftNameResolver.create(widebodyAircraftDatabaseProvider);
  }

  // Database
  @Bean
  public UserDatabaseProvider userDatabaseProvider(
      UserRepository userRepository, GeneralConfig generalConfig) {
    return UserDatabaseProvider.create(userRepository, generalConfig);
  }

  @Bean
  public WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider(
      WidebodyAircraftRepository widebodyAircraftRepository) {
    return WidebodyAircraftDatabaseProvider.create(widebodyAircraftRepository);
  }

  // Other required beans
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClient.newHttpClient();
  }

  @Bean
  @ConfigurationProperties(prefix = "general")
  public GeneralConfig generalConfig() {
    return new GeneralConfig();
  }
}
