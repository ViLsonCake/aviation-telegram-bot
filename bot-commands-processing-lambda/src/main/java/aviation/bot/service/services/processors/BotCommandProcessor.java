package aviation.bot.service.services.processors;

import project.vilsoncake.common.models.BotCommand;

public interface BotCommandProcessor {
  BotCommand getProcessorCommand();

  void process(String username, long chatId, String text);
}
