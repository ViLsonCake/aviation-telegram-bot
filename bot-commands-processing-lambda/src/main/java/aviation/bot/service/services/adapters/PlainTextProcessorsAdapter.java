package aviation.bot.service.services.adapters;

import aviation.bot.service.services.processors.PlainTextProcessor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import project.vilsoncake.common.entities.enums.UserState;

public class PlainTextProcessorsAdapter {

  private final Map<UserState, PlainTextProcessor> plainTextProcessorMap;

  public static PlainTextProcessorsAdapter create(List<PlainTextProcessor> plainTextProcessors) {
    return new PlainTextProcessorsAdapter(plainTextProcessors);
  }

  private PlainTextProcessorsAdapter(List<PlainTextProcessor> plainTextProcessors) {
    this.plainTextProcessorMap =
        plainTextProcessors.stream()
            .collect(
                LinkedHashMap::new,
                (map, processor) -> map.put(processor.getUserState(), processor),
                Map::putAll);
  }

  public void process(UserState userState, String username, long chatId, String text) {
    PlainTextProcessor plainTextProcessor = plainTextProcessorMap.get(userState);

    // Silently ignore unknown user states
    if (plainTextProcessor == null) {
      return;
    }

    plainTextProcessor.process(username, chatId, text);
  }
}
