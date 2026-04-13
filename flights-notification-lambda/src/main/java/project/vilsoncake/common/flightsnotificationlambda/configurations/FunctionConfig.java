package project.vilsoncake.common.flightsnotificationlambda.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Configuration
public class FunctionConfig {

    @Bean
    public Function<Map<String, String>, String> flightsNotificationHandler() {
        return payload -> {
            // TODO: process flight notifications
            return "OK";
        };
    }
}
