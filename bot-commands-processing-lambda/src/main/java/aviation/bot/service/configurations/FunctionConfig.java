package aviation.bot.service.configurations;

import aviation.bot.service.handlers.TelegramHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FunctionConfig {

  private final TelegramHandler telegramHandler;

  @Bean
  public Function<APIGatewayV2HTTPEvent, String> botCommandsHandler() {
    return telegramHandler::handleRequest;
  }
}
