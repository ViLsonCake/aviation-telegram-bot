package aviation.bot.service.services.adapters;

import aviation.bot.service.services.processors.CallbackProcessor;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.models.CallbackType;

@RequiredArgsConstructor(staticName = "create")
public class CallbackProcessorsAdapter {

  private final Map<CallbackType, CallbackProcessor> callbackProcessors = new LinkedHashMap<>();

  public void registerCallbackProcessor(CallbackProcessor callbackProcessor) {
    callbackProcessors.put(callbackProcessor.getCallbackType(), callbackProcessor);
  }

  public void process(CallbackType callbackType, String username, long chatId, String callbackId) {
    CallbackProcessor callbackProcessor = callbackProcessors.get(callbackType);

    // Silently ignore unknown callback types
    if (callbackProcessor == null) {
      return;
    }

    callbackProcessor.process(username, chatId, callbackId);
  }
}
