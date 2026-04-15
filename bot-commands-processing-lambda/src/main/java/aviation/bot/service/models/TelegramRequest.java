package aviation.bot.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class TelegramRequest {
  private MessageContentType messageContentType;
  private TelegramRequestPayload payload;
}
