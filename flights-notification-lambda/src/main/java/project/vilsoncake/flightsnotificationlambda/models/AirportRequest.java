package project.vilsoncake.flightsnotificationlambda.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class AirportRequest {
  @JsonProperty("airport_code")
  private String airportCode;

  @JsonProperty("aircraft_filter_codes")
  private List<String> aircraftFilterCodes;
}
