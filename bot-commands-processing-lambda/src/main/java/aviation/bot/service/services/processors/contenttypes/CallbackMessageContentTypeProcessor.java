package aviation.bot.service.services.processors.contenttypes;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.models.TelegramRequestPayload;
import aviation.bot.service.services.adapters.CallbackProcessorsAdapter;
import aviation.bot.service.services.processors.MessageContentTypeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.models.CallbackType;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class CallbackMessageContentTypeProcessor implements MessageContentTypeProcessor {

  private final CallbackProcessorsAdapter callbackProcessorsAdapter;

  @Override
  public MessageContentType getMessageContentType() {
    return MessageContentType.CALLBACK;
  }

  @Override
  public void process(TelegramRequestPayload telegramRequestPayload) {
    String username = telegramRequestPayload.getUsername();
    long chatId = telegramRequestPayload.getChatId();
    String callbackData = telegramRequestPayload.getCallbackData();
    String callbackId = telegramRequestPayload.getCallbackId();
    CallbackType callbackType;

    if (callbackData == null) {
      throw new IllegalArgumentException("Callback data cannot be null");
    }

    if (callbackId == null) {
      throw new IllegalArgumentException("Callback id cannot be null");
    }

    try {
      callbackType = CallbackType.valueOf(callbackData);
    } catch (IllegalArgumentException e) {
      log.warn("Unknown callback type: {}", callbackData);
      return;
    }

    callbackProcessorsAdapter.process(callbackType, username, chatId, callbackId);
  }
}
