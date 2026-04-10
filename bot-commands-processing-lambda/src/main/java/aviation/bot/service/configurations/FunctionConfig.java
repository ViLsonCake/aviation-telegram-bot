package aviation.bot.service.configurations;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class FunctionConfig {

    @Bean
    public Function<APIGatewayV2HTTPEvent, String> botCommandsHandler() {
        return event -> {
            // TODO: dispatch bot commands
            return "OK";
        };
    }
}
