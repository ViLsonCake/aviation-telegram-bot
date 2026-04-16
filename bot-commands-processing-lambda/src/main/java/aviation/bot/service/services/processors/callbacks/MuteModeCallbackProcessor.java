package aviation.bot.service.services.processors.callbacks;

import aviation.bot.service.services.BotModeChangeService;
import aviation.bot.service.services.processors.CallbackProcessor;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.models.CallbackType;

@RequiredArgsConstructor(staticName = "create")
public class MuteModeCallbackProcessor implements CallbackProcessor {

  private final BotModeChangeService botModeChangeService;

  @Override
  public CallbackType getCallbackType() {
    return CallbackType.MUTE_BOT_MODE_SELECTED;
  }

  @Override
  public void process(String username, long chatId, String callbackId) {
    botModeChangeService.handleCallback(getCallbackType(), username, chatId, callbackId);
  }
}
