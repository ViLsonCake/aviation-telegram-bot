package aviation.bot.service.configurations;

import aviation.bot.service.handlers.TelegramHandler;
import aviation.bot.service.services.CommandMessageContentTypeProcessor;
import aviation.bot.service.services.PingBotCommandProcessor;
import aviation.bot.service.services.adapters.BotCommandAdapter;
import aviation.bot.service.services.adapters.MessageContentTypeAdapter;
import aviation.bot.service.services.clients.TelegramClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

  @Bean
  public TelegramClient telegramClient(HttpClient httpClient, BotConfig botConfig) {
    return TelegramClient.create(httpClient, botConfig);
  }

  @Bean
  public PingBotCommandProcessor pingBotCommandProcessor(TelegramClient telegramClient) {
    return PingBotCommandProcessor.create(telegramClient);
  }

  @Bean
  public BotCommandAdapter botCommandAdapter(PingBotCommandProcessor pingBotCommandProcessor) {
    BotCommandAdapter botCommandAdapter = BotCommandAdapter.create();
    botCommandAdapter.registerCommandProcessor(pingBotCommandProcessor);
    return botCommandAdapter;
  }

  @Bean
  public CommandMessageContentTypeProcessor commandMessageContentTypeProcessor(BotCommandAdapter botCommandAdapter) {
    return CommandMessageContentTypeProcessor.create(botCommandAdapter);
  }

  @Bean
  public MessageContentTypeAdapter messageContentTypeAdapter(BotCommandAdapter botCommandAdapter) {
    MessageContentTypeAdapter messageContentTypeAdapter = MessageContentTypeAdapter.create();
    messageContentTypeAdapter.registerMessageContentTypeProcessor(commandMessageContentTypeProcessor(botCommandAdapter));
    return messageContentTypeAdapter;
  }

  @Bean
  public TelegramHandler telegramHandler(MessageContentTypeAdapter messageContentTypeAdapter, ObjectMapper objectMapper) {
    return TelegramHandler.create(messageContentTypeAdapter, objectMapper);
  }
}
