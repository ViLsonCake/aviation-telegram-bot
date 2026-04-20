package aviation.bot.service.services.adapters;

import aviation.bot.service.services.processors.CallbackProcessor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import project.vilsoncake.common.models.CallbackType;

public class CallbackProcessorsAdapter {

  private final Map<CallbackType, CallbackProcessor> callbackProcessorsMap;

  public static CallbackProcessorsAdapter create(List<CallbackProcessor> callbackProcessors) {
    return new CallbackProcessorsAdapter(callbackProcessors);
  }

  private CallbackProcessorsAdapter(List<CallbackProcessor> callbackProcessors) {
    this.callbackProcessorsMap =
        callbackProcessors.stream()
            .collect(
                LinkedHashMap::new,
                (map, processor) -> map.put(processor.getCallbackType(), processor),
                Map::putAll);
  }

  public void process(CallbackType callbackType, String username, long chatId, String callbackId) {
    CallbackProcessor callbackProcessor = callbackProcessorsMap.get(callbackType);

    // Silently ignore unknown callback types
    if (callbackProcessor == null) {
      return;
    }

    callbackProcessor.process(username, chatId, callbackId);
  }
}
