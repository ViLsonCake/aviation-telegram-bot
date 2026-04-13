package aviation.bot.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "project.vilsoncake.common.repositories")
@EntityScan(basePackages = "project.vilsoncake.common.entities")
public class BotCommandsProcessingLambdaApplication {

  public static void main(String[] args) {
    SpringApplication.run(BotCommandsProcessingLambdaApplication.class, args);
  }

}
