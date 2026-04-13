package aviation.bot.service.services;

import aviation.bot.service.models.MessageContentType;

public interface MessageContentTypeProcessor {
  MessageContentType getMessageContentType();
  void process(long chatId, String text);
}
