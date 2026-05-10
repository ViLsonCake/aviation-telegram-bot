package project.vilsoncake.common.clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.telegram.AnswerCallbackQueryRequest;
import project.vilsoncake.common.clients.telegram.InlineKeyboardMarkup;
import project.vilsoncake.common.clients.telegram.InputMediaPhoto;
import project.vilsoncake.common.clients.telegram.SendMediaGroupRequest;
import project.vilsoncake.common.clients.telegram.SendMessageRequest;
import project.vilsoncake.common.clients.telegram.TelegramRequest;
import project.vilsoncake.common.configurations.BotConfig;
import project.vilsoncake.common.models.FlightImage;
import tools.jackson.databind.ObjectMapper;

/** Telegram HTTP client to interact with Telegram API. */
@RequiredArgsConstructor(staticName = "create")
public class TelegramClient {

  private final HttpClient httpClient;
  private final BotConfig botConfig;
  private final ObjectMapper objectMapper;

  // DO NOT UPDATE: strict Telegram API limit
  private static final int MAX_MESSAGE_LENGTH = 4096;

  // DO NOT UPDATE: strict Telegram API limit for media group captions
  private static final int MAX_CAPTION_LENGTH = 1024;

  // DO NOT UPDATE: strict Telegram API limit for media group size
  private static final int MAX_MEDIA_GROUP_SIZE = 10;

  /**
   * Sends multiple plain text messages to Telegram API following the order.
   *
   * @param chatId chat id to send the message to
   * @param messages messages to send
   */
  public void sendMessages(long chatId, List<String> messages) {
    messages.forEach(message -> sendMessage(chatId, message));
  }

  /**
   * Sends a plain text message to Telegram API.
   *
   * @param chatId chat id to send the message to
   * @param messageText message text to send
   */
  public void sendMessage(long chatId, String messageText) {
    if (messageText.length() > MAX_MESSAGE_LENGTH) {
      throw new IllegalArgumentException(
          "Message text is too long. Maximum length is " + MAX_MESSAGE_LENGTH);
    }

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
    if (messageText.length() > MAX_MESSAGE_LENGTH) {
      throw new IllegalArgumentException(
          "Message text is too long. Maximum length is " + MAX_MESSAGE_LENGTH);
    }

    SendMessageRequest request =
        SendMessageRequest.builder()
            .withChatId(chatId)
            .withText(normalizeMessageText(messageText))
            .withReplyMarkup(replyMarkup)
            .build();
    dispatchMessage(request);
  }

  /**
   * Sends a media group (album) of photos with the message text as caption on the first photo.
   * Falls back to a plain text message if images list is null or empty.
   *
   * @param chatId chat id to send to
   * @param messageText text to use as caption (or plain message if no images)
   * @param images list of flight images
   */
  public void sendMessageWithImages(long chatId, String messageText, List<FlightImage> images) {
    if (images == null || images.isEmpty()) {
      sendMessage(chatId, messageText);
      return;
    }

    String caption = normalizeMessageText(messageText);
    if (caption.length() > MAX_CAPTION_LENGTH) {
      caption = caption.substring(0, MAX_CAPTION_LENGTH);
    }

    List<InputMediaPhoto> media = new ArrayList<>();
    List<FlightImage> capped = images.subList(0, Math.min(images.size(), MAX_MEDIA_GROUP_SIZE));
    for (int i = 0; i < capped.size(); i++) {
      media.add(
          i == 0
              ? new InputMediaPhoto(capped.get(i).getSrc(), caption)
              : new InputMediaPhoto(capped.get(i).getSrc()));
    }

    SendMediaGroupRequest request = new SendMediaGroupRequest(chatId, media);
    dispatch(request, botConfig.getTelegramApiMediaGroupUrl());
  }

  public void answerCallbackQuery(String callbackQueryId) {
    AnswerCallbackQueryRequest request =
        AnswerCallbackQueryRequest.builder().withCallbackQueryId(callbackQueryId).build();
    dispatchCallbackAnswer(request);
  }

  public void answerCallbackQueryWithAlert(String callbackQueryId, String text) {
    AnswerCallbackQueryRequest request =
        AnswerCallbackQueryRequest.builder()
            .withCallbackQueryId(callbackQueryId)
            .withText(text)
            .withShowAlert(true)
            .build();
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

  private static final Pattern MARKDOWN_LINK_PATTERN = Pattern.compile("\\[([^]]+)]\\(([^)]+)\\)");

  /** Normalize message text for Telegram API MarkdownV2 format. */
  private String normalizeMessageText(String messageText) {
    Matcher matcher = MARKDOWN_LINK_PATTERN.matcher(messageText);
    List<String> links = new ArrayList<>();
    StringBuilder withPlaceholders = new StringBuilder();

    while (matcher.find()) {
      links.add("[" + matcher.group(1) + "](" + matcher.group(2) + ")");
      matcher.appendReplacement(withPlaceholders, "MDLINKPLACEHOLDER" + (links.size() - 1) + "END");
    }
    matcher.appendTail(withPlaceholders);

    String escaped =
        withPlaceholders
            .toString()
            .replace("_", "\\_")
            .replace("*", "\\*")
            .replace("[", "\\[")
            .replace("]", "\\]")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("~", "\\~")
            .replace("`", "\\`")
            .replace(">", "\\>")
            .replace("#", "\\#")
            .replace("+", "\\+")
            .replace("-", "\\-")
            .replace("=", "\\=")
            .replace("|", "\\|")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace(".", "\\.")
            .replace("!", "\\!")
            .replace("\"", "\\\"");

    for (int i = 0; i < links.size(); i++) {
      escaped = escaped.replace("MDLINKPLACEHOLDER" + i + "END", links.get(i));
    }

    return escaped;
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
