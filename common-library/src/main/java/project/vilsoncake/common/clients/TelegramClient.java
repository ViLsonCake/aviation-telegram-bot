package project.vilsoncake.common.clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.telegram.AnswerCallbackQueryRequest;
import project.vilsoncake.common.clients.telegram.InlineKeyboardMarkup;
import project.vilsoncake.common.clients.telegram.SendMessageRequest;
import project.vilsoncake.common.clients.telegram.TelegramRequest;
import project.vilsoncake.common.configurations.BotConfig;
import tools.jackson.databind.ObjectMapper;

/** Telegram HTTP client to interact with Telegram API. */
@RequiredArgsConstructor(staticName = "create")
public class TelegramClient {

  private final HttpClient httpClient;
  private final BotConfig botConfig;
  private final ObjectMapper objectMapper;

  /**
   * Sends a plain text message to Telegram API.
   *
   * @param chatId chat id to send the message to
   * @param messageText message text to send
   */
  public void sendMessage(long chatId, String messageText) {
    SendMessageRequest request =
        SendMessageRequest.builder()
            .withChatId(chatId)
            .withText(normalizeMessageText(messageText))
            .build();
    dispatchMessage(request);
  }

  /**
   * Sends a message with an inline keyboard to Telegram API.
   *
   * @param chatId chat id to send the message to
   * @param messageText message text to send
   * @param replyMarkup inline keyboard to attach
   */
  public void sendMessage(long chatId, String messageText, InlineKeyboardMarkup replyMarkup) {
    SendMessageRequest request =
        SendMessageRequest.builder()
            .withChatId(chatId)
            .withText(normalizeMessageText(messageText))
            .withReplyMarkup(replyMarkup)
            .build();
    dispatchMessage(request);
  }

  public void answerCallbackQuery(String callbackQueryId) {
    AnswerCallbackQueryRequest request =
        AnswerCallbackQueryRequest.builder().withCallbackQueryId(callbackQueryId).build();
    dispatchCallbackAnswer(request);
  }

  private void dispatchMessage(TelegramRequest telegramRequest) {
    dispatch(telegramRequest, botConfig.getTelegramApiUrl());
  }

  private void dispatchCallbackAnswer(TelegramRequest telegramRequest) {
    dispatch(telegramRequest, botConfig.getTelegramApiCallbackUrl());
  }

  private void dispatch(TelegramRequest telegramRequest, String url) {
    try {
      String body = objectMapper.writeValueAsString(telegramRequest);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new TelegramClientException(
            "Telegram API returned non-200 status code: "
                + response.statusCode()
                + ", body: "
                + response.body());
      }
    } catch (TelegramClientException e) {
      throw e;
    } catch (Exception e) {
      throw new TelegramClientException(
          "An exception occurred while sending message to Telegram API", e);
    }
  }

  /** Normalize message text for Telegram API MarkdownV2 format. */
  private String normalizeMessageText(String messageText) {
    return messageText
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("[", "\\\\[")
        .replace("]", "\\\\]")
        .replace("(", "\\\\(")
        .replace(")", "\\\\)")
        .replace("~", "\\\\~")
        .replace("`", "\\\\`")
        .replace(">", "\\\\>")
        .replace("#", "\\\\#")
        .replace("+", "\\\\+")
        .replace("-", "\\\\-")
        .replace("=", "\\\\=")
        .replace("|", "\\\\|")
        .replace("{", "\\\\{")
        .replace("}", "\\\\}")
        .replace(".", "\\\\.")
        .replace("!", "\\\\!")
        .replace("\"", "\\\"");
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
