package aviation.bot.service.services.processors;

import aviation.bot.service.models.MessageContentType;

public interface MessageContentTypeProcessor {
  MessageContentType getMessageContentType();

  void process(String username, long chatId, String text);
}
