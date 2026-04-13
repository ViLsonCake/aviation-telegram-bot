package project.vilsoncake.common.clients;

import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.configurations.BotConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Telegram HTTP client to interact with Telegram API. */
@RequiredArgsConstructor(staticName = "create")
public class TelegramClient {

  private final HttpClient httpClient;
  private final BotConfig botConfig;

  private static final String BODY_TEMPLATE = """
      {
          "chat_id": %d,
          "text": "%s",
          "parse_mode": "MarkdownV2"
      }
      """;

  /**
   * Sends a message to Telegram API.
   *
   * @param chatId chat id to send the message to
   * @param messageText message text to send
   */
  public void sendMessage(long chatId, String messageText) {
    String body = constructBody(chatId, messageText);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(botConfig.getTelegramApiUrl()))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new TelegramClientException("Telegram API returned non-200 status code: " + response.statusCode());
      }
    } catch (Exception e) {
      throw new TelegramClientException("An exception occurred while sending message to Telegram API", e);
    }
  }

  /** Construct a body for a request to Telegram API from the given parameters. */
  private String constructBody(long chatId, String messageText) {
    return String.format(BODY_TEMPLATE, chatId, normalizeMessageText(messageText));
  }

  /** Normalize message text for Telegram API MarkdownV2 format. */
  private String normalizeMessageText(String messageText) {
    return messageText.replace("-", "\\\\-").replace("\"", "\\\"");
  }

  public static class TelegramClientException extends RuntimeException {
    public TelegramClientException(String message) {
      super(message);
    }

    public TelegramClientException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
