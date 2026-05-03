package aviation.bot.service.services.processors;

import project.vilsoncake.common.models.CallbackType;

public interface CallbackProcessor {
  CallbackType getCallbackType();

  void process(String username, long chatId, String callbackData, String callbackId);
}
