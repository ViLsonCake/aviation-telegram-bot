package project.vilsoncake.flightsnotificationlambda.configurations;

import java.net.http.HttpClient;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.configurations.AwsConfig;
import project.vilsoncake.common.configurations.BotConfig;
import project.vilsoncake.common.configurations.GeneralConfig;
import project.vilsoncake.common.configurations.NotificationsConfig;
import project.vilsoncake.common.repositories.AircraftFamilyRepository;
import project.vilsoncake.common.repositories.AirportDatabaseProvider;
import project.vilsoncake.common.repositories.AirportRepository;
import project.vilsoncake.common.repositories.ScheduledFlightDatabaseProvider;
import project.vilsoncake.common.repositories.ScheduledFlightNotificationDatabaseProvider;
import project.vilsoncake.common.repositories.ScheduledFlightNotificationRepository;
import project.vilsoncake.common.repositories.ScheduledFlightRepository;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterDatabaseProvider;
import project.vilsoncake.common.repositories.UserAircraftFamilyFilterRepository;
import project.vilsoncake.common.repositories.UserDatabaseProvider;
import project.vilsoncake.common.repositories.UserRepository;
import project.vilsoncake.common.repositories.WidebodyAircraftDatabaseProvider;
import project.vilsoncake.common.repositories.WidebodyAircraftRepository;
import project.vilsoncake.common.services.adapters.FlightradarApiLambdaAdapter;
import project.vilsoncake.flightsnotificationlambda.handlers.FlightsNotificationTriggerHandler;
import project.vilsoncake.flightsnotificationlambda.models.BotTemplates;
import project.vilsoncake.flightsnotificationlambda.processors.FlightStatusChangeNotificationTypeProcessor;
import project.vilsoncake.flightsnotificationlambda.processors.NotificationTypeProcessor;
import project.vilsoncake.flightsnotificationlambda.processors.ScheduledFlightsNotificationTypeProcessor;
import project.vilsoncake.flightsnotificationlambda.services.AircraftNameResolver;
import project.vilsoncake.flightsnotificationlambda.services.BotTemplatesResolver;
import project.vilsoncake.flightsnotificationlambda.services.FlightStatusChangeNotificationSender;
import project.vilsoncake.flightsnotificationlambda.services.ScheduledFlightNotificationFlagsResolver;
import project.vilsoncake.flightsnotificationlambda.services.ScheduledFlightsNotificationSender;
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
  public FlightStatusChangeNotificationTypeProcessor flightStatusChangeNotificationTypeProcessor(
      ScheduledFlightNotificationDatabaseProvider scheduledFlightNotificationDatabaseProvider,
      WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider,
      FlightradarApiLambdaAdapter flightradarApiLambdaAdapter,
      FlightStatusChangeNotificationSender flightStatusChangeNotificationSender) {
    return FlightStatusChangeNotificationTypeProcessor.create(
        scheduledFlightNotificationDatabaseProvider,
        widebodyAircraftDatabaseProvider,
        flightradarApiLambdaAdapter,
        flightStatusChangeNotificationSender);
  }

  @Bean
  public ScheduledFlightsNotificationTypeProcessor scheduledFlightsNotificationTypeProcessor(
      UserDatabaseProvider userDatabaseProvider,
      WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider,
      UserAircraftFamilyFilterDatabaseProvider userAircraftFamilyFilterDatabaseProvider,
      FlightradarApiLambdaAdapter flightradarApiLambdaAdapter,
      ScheduledFlightsNotificationSender scheduledFlightsNotificationSender) {
    return ScheduledFlightsNotificationTypeProcessor.create(
        userDatabaseProvider,
        widebodyAircraftDatabaseProvider,
        userAircraftFamilyFilterDatabaseProvider,
        flightradarApiLambdaAdapter,
        scheduledFlightsNotificationSender);
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
  public ScheduledFlightsNotificationSender notificationsService(
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver,
      AircraftNameResolver aircraftNameResolver,
      ScheduledFlightDatabaseProvider scheduledFlightDatabaseProvider,
      ScheduledFlightNotificationDatabaseProvider scheduledFlightNotificationDatabaseProvider,
      ScheduledFlightNotificationFlagsResolver scheduledFlightNotificationFlagsResolver) {
    return ScheduledFlightsNotificationSender.create(
        telegramClient,
        botTemplatesResolver,
        aircraftNameResolver,
        scheduledFlightDatabaseProvider,
        scheduledFlightNotificationDatabaseProvider,
        scheduledFlightNotificationFlagsResolver);
  }

  @Bean
  public AircraftNameResolver aircraftNameResolver(
      WidebodyAircraftDatabaseProvider widebodyAircraftDatabaseProvider) {
    return AircraftNameResolver.create(widebodyAircraftDatabaseProvider);
  }

  @Bean
  public ScheduledFlightNotificationFlagsResolver scheduledFlightNotificationFlagsResolver(
      NotificationsConfig notificationsConfig) {
    return ScheduledFlightNotificationFlagsResolver.create(notificationsConfig);
  }

  @Bean
  public FlightStatusChangeNotificationSender flightStatusChangeSender(
      TelegramClient telegramClient,
      BotTemplatesResolver botTemplatesResolver,
      AircraftNameResolver aircraftNameResolver,
      ScheduledFlightNotificationDatabaseProvider scheduledFlightNotificationDatabaseProvider,
      ScheduledFlightDatabaseProvider scheduledFlightDatabaseProvider,
      AirportDatabaseProvider airportDatabaseProvider,
      NotificationsConfig notificationsConfig) {
    return FlightStatusChangeNotificationSender.create(
        telegramClient,
        botTemplatesResolver,
        aircraftNameResolver,
        scheduledFlightNotificationDatabaseProvider,
        scheduledFlightDatabaseProvider,
        airportDatabaseProvider,
        notificationsConfig);
  }

  @Bean
  public AirportDatabaseProvider airportDatabaseProvider(AirportRepository airportRepository) {
    return AirportDatabaseProvider.create(airportRepository);
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

  @Bean
  public UserAircraftFamilyFilterDatabaseProvider userAircraftFamilyFilterDatabaseProvider(
      UserAircraftFamilyFilterRepository filterRepository,
      AircraftFamilyRepository aircraftFamilyRepository) {
    return UserAircraftFamilyFilterDatabaseProvider.create(
        filterRepository, aircraftFamilyRepository);
  }

  @Bean
  public ScheduledFlightDatabaseProvider scheduledFlightDatabaseProvider(
      ScheduledFlightRepository scheduledFlightRepository,
      AirportRepository airportRepository,
      WidebodyAircraftRepository widebodyAircraftRepository) {
    return new ScheduledFlightDatabaseProvider(
        scheduledFlightRepository, airportRepository, widebodyAircraftRepository);
  }

  @Bean
  public ScheduledFlightNotificationDatabaseProvider scheduledFlightNotificationDatabaseProvider(
      ScheduledFlightNotificationRepository scheduledFlightNotificationRepository) {
    return new ScheduledFlightNotificationDatabaseProvider(scheduledFlightNotificationRepository);
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

  @Bean
  @ConfigurationProperties(prefix = "notifications")
  public NotificationsConfig notificationsConfig() {
    return new NotificationsConfig();
  }
}
