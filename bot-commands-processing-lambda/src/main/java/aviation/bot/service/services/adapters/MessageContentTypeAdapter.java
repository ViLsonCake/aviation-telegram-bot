package aviation.bot.service.services.adapters;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.models.TelegramRequestPayload;
import aviation.bot.service.services.processors.MessageContentTypeProcessor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageContentTypeAdapter {

  private final Map<MessageContentType, MessageContentTypeProcessor> messageContentTypeProcessorMap;

  public static MessageContentTypeAdapter create(
      List<MessageContentTypeProcessor> messageContentTypeProcessors) {
    return new MessageContentTypeAdapter(messageContentTypeProcessors);
  }

  private MessageContentTypeAdapter(
      List<MessageContentTypeProcessor> messageContentTypeProcessors) {
    this.messageContentTypeProcessorMap =
        messageContentTypeProcessors.stream()
            .collect(
                LinkedHashMap::new,
                (map, processor) -> map.put(processor.getMessageContentType(), processor),
                Map::putAll);
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
