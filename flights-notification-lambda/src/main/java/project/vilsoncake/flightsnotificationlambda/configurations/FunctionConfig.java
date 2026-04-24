package project.vilsoncake.flightsnotificationlambda.configurations;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.vilsoncake.flightsnotificationlambda.handlers.FlightsNotificationTriggerHandler;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationTriggerPayload;

@Configuration
@RequiredArgsConstructor
public class FunctionConfig {

  private final FlightsNotificationTriggerHandler flightsNotificationTriggerHandler;

  @Bean
  public Function<FlightsNotificationTriggerPayload, String> flightsNotificationHandler() {
    return flightsNotificationTriggerHandler::handle;
  }
}
