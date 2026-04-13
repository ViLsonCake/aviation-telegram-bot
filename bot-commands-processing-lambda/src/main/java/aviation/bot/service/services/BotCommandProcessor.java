package aviation.bot.service.services;

import aviation.bot.service.models.BotCommand;

public interface BotCommandProcessor {
  BotCommand getProcessorCommand();
  void process(long chatId, String text);
}
