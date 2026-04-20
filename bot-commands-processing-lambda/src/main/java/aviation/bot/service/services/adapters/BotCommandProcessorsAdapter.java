package aviation.bot.service.services.adapters;

import aviation.bot.service.services.processors.BotCommandProcessor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BotCommandProcessorsAdapter {

  private final Map<String, BotCommandProcessor> commandProcessorMap;

  public static BotCommandProcessorsAdapter create(List<BotCommandProcessor> botCommandProcessors) {
    return new BotCommandProcessorsAdapter(botCommandProcessors);
  }

  private BotCommandProcessorsAdapter(List<BotCommandProcessor> botCommandProcessors) {
    this.commandProcessorMap =
        botCommandProcessors.stream()
            .collect(
                LinkedHashMap::new,
                (map, processor) ->
                    map.put(processor.getProcessorCommand().getCommand(), processor),
                Map::putAll);
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
