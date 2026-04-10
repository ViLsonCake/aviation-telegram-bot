package aviation.bot.service.services.adapters;

import aviation.bot.service.services.BotCommandProcessor;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public class BotCommandAdapter {

  private final Map<String, BotCommandProcessor> commandProcessorMap = new LinkedHashMap<>();

  public void registerCommandProcessor(BotCommandProcessor commandProcessor) {
    commandProcessorMap.put(commandProcessor.getProcessorCommand().getCommand(), commandProcessor);
  }

  public void process(long chatId, String command) {
    BotCommandProcessor botCommandProcessor = commandProcessorMap.get(command);

    // Silently ignore unknown commands
    if (botCommandProcessor == null) {
      return;
    }

    botCommandProcessor.process(chatId, command);
  }
}
