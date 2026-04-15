package aviation.bot.service.services.adapters;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.models.TelegramRequestPayload;
import aviation.bot.service.services.processors.MessageContentTypeProcessor;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class MessageContentTypeAdapter {

  private final Map<MessageContentType, MessageContentTypeProcessor>
      messageContentTypeProcessorMap = new LinkedHashMap<>();

  public void registerMessageContentTypeProcessor(
      MessageContentTypeProcessor messageContentTypeProcessor) {
    messageContentTypeProcessorMap.put(
        messageContentTypeProcessor.getMessageContentType(), messageContentTypeProcessor);
  }

  public void process(
      MessageContentType messageContentType, TelegramRequestPayload telegramRequestPayload) {
    MessageContentTypeProcessor messageContentTypeProcessor =
        messageContentTypeProcessorMap.get(messageContentType);

    // Silently ignore unknown message content types
    if (messageContentTypeProcessor == null) {
      return;
    }

    messageContentTypeProcessor.process(telegramRequestPayload);
  }
}
