package aviation.bot.service.services.adapters;

import aviation.bot.service.services.BotCommandProcessor;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public class BotCommandProcessorsAdapter {

  private final Map<String, BotCommandProcessor> commandProcessorMap = new LinkedHashMap<>();

  public void registerCommandProcessor(BotCommandProcessor commandProcessor) {
    commandProcessorMap.put(commandProcessor.getProcessorCommand().getCommand(), commandProcessor);
  }

  public void process(String username, long chatId, String command) {
    BotCommandProcessor botCommandProcessor = commandProcessorMap.get(command);

    // Silently ignore unknown commands
    if (botCommandProcessor == null) {
      return;
    }

    botCommandProcessor.process(username, chatId, command);
  }
}
