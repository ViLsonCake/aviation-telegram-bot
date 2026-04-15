package aviation.bot.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class TelegramRequestPayload {
  private String username;
  private long chatId;
  private String text;
  private String callbackData;
  private String callbackId;
}
