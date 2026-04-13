package aviation.bot.service.services.adapters;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.services.MessageContentTypeProcessor;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public class MessageContentTypeAdapter {

  private final Map<MessageContentType, MessageContentTypeProcessor> messageContentTypeProcessorMap = new LinkedHashMap<>();

  public void registerMessageContentTypeProcessor(MessageContentTypeProcessor messageContentTypeProcessor) {
    messageContentTypeProcessorMap.put(messageContentTypeProcessor.getMessageContentType(), messageContentTypeProcessor);
  }

  public void process(MessageContentType messageContentType, String username, long chatId, String text) {
    MessageContentTypeProcessor messageContentTypeProcessor = messageContentTypeProcessorMap.get(messageContentType);

    // Silently ignore unknown message content types
    if (messageContentTypeProcessor == null) {
      return;
    }

    messageContentTypeProcessor.process(username, chatId, text);
  }
}
