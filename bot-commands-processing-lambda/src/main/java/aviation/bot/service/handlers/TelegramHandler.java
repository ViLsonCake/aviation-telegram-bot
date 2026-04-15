package aviation.bot.service.handlers;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.models.TelegramRequest;
import aviation.bot.service.models.TelegramRequestPayload;
import aviation.bot.service.services.adapters.MessageContentTypeAdapter;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor(staticName = "create")
public class TelegramHandler {

  private final MessageContentTypeAdapter messageContentTypeAdapter;
  private final ObjectMapper objectMapper;

  public String handleRequest(APIGatewayV2HTTPEvent event) {
    String body = event.getBody();

    JsonNode root = objectMapper.readTree(body);

    TelegramRequest telegramRequest = buildTelegramRequest(root);

    messageContentTypeAdapter.process(
        telegramRequest.getMessageContentType(), telegramRequest.getPayload());

    return "OK";
  }

  private TelegramRequest buildTelegramRequest(JsonNode root) {
    JsonNode callbackQuery = root.path("callback_query");
    JsonNode message = root.path("message");

    if (!callbackQuery.isMissingNode()) {
      long chatId = callbackQuery.path("message").path("chat").path("id").asLong();
      String username = callbackQuery.path("from").path("username").asString(null);
      String callbackData = callbackQuery.path("data").asString(null);
      String callbackId = callbackQuery.path("id").asString(null);

      return TelegramRequest.builder()
          .withMessageContentType(MessageContentType.CALLBACK)
          .withPayload(
              TelegramRequestPayload.builder()
                  .withChatId(chatId)
                  .withUsername(username)
                  .withCallbackData(callbackData)
                  .withCallbackId(callbackId)
                  .build())
          .build();
    } else if (!message.isMissingNode()) {
      long chatId = message.path("chat").path("id").asLong();
      String username = message.path("chat").path("username").asString(null);
      String text = message.path("text").asString("");
      MessageContentType messageContentType = getMessageContentType(text);

      return TelegramRequest.builder()
          .withMessageContentType(messageContentType)
          .withPayload(
              TelegramRequestPayload.builder()
                  .withChatId(chatId)
                  .withUsername(username)
                  .withText(text)
                  .build())
          .build();
    }

    throw new IllegalArgumentException("Unsupported request");
  }

  private MessageContentType getMessageContentType(String messageText) {
    return messageText.startsWith("/") ? MessageContentType.COMMAND : MessageContentType.PLAIN_TEXT;
  }
}
