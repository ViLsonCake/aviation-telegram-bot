package aviation.bot.service.configurations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotConfig {
  private String token;
  private String telegramApiUrl;
}
