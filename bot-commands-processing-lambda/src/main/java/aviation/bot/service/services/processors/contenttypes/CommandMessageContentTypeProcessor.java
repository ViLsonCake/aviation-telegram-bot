package aviation.bot.service.services.processors.contenttypes;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.services.adapters.BotCommandProcessorsAdapter;
import aviation.bot.service.services.processors.MessageContentTypeProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class CommandMessageContentTypeProcessor implements MessageContentTypeProcessor {

  private final BotCommandProcessorsAdapter botCommandProcessorsAdapter;

  @Override
  public MessageContentType getMessageContentType() {
    return MessageContentType.COMMAND;
  }

  @Override
  public void process(String username, long chatId, String text) {
    botCommandProcessorsAdapter.process(username, chatId, text);
  }
}
