package project.vilsoncake.common.configurations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotConfig {
  private String token;
  private String telegramApiUrl;
  private String telegramApiCallbackUrl;
  private String telegramApiMediaGroupUrl;
}
