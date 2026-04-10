package aviation.bot.service.services;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.services.adapters.BotCommandAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class CommandMessageContentTypeProcessor implements MessageContentTypeProcessor {

  private final BotCommandAdapter botCommandAdapter;

  @Override
  public MessageContentType getMessageContentType() {
    return MessageContentType.COMMAND;
  }

  @Override
  public void process(long chatId, String text) {
    botCommandAdapter.process(chatId, text);
  }
}
