package project.vilsoncake.flightsnotificationlambda.configurations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwsConfig {
  private String region;
  private String flightradarApiLambdaName;
}
