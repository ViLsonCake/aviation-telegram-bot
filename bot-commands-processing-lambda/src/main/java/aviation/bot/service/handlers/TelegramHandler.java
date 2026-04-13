package aviation.bot.service.handlers;

import aviation.bot.service.models.MessageContentType;
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

    JsonNode message = root.path("message");
    if (message.isMissingNode()) {
      return "OK";
    }

    long chatId = message.path("chat").path("id").asLong();
    String username = message.path("chat").path("username").asString(null);
    String text = message.path("text").asString("");

    MessageContentType messageContentType = getMessageContent(text);

    messageContentTypeAdapter.process(messageContentType, username, chatId, text);

    return "OK";
  }

  private MessageContentType getMessageContent(String messageText) {
    return messageText.startsWith("/") ? MessageContentType.COMMAND : MessageContentType.PLAIN_TEXT;
  }
}
