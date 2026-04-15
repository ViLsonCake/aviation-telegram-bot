package aviation.bot.service.services.processors;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.models.TelegramRequestPayload;

public interface MessageContentTypeProcessor {
  MessageContentType getMessageContentType();

  void process(TelegramRequestPayload telegramRequestPayload);
}
