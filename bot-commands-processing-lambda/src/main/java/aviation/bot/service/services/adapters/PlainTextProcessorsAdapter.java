package aviation.bot.service.services.adapters;

import aviation.bot.service.services.PlainTextProcessor;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.entities.enums.UserState;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public class PlainTextProcessorsAdapter {

  private final Map<UserState, PlainTextProcessor> plainTextProcessorMap = new LinkedHashMap<>();

  public void registerPlainTextProcessor(PlainTextProcessor plainTextProcessor) {
    plainTextProcessorMap.put(plainTextProcessor.getUserState(), plainTextProcessor);
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
