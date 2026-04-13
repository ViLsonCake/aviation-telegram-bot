package aviation.bot.service.services;

import project.vilsoncake.common.entities.enums.UserState;

public interface PlainTextProcessor {
  UserState getUserState();
  void process(String username, long chatId, String text);
}
