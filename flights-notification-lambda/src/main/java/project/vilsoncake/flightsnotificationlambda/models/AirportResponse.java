package project.vilsoncake.flightsnotificationlambda.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.vilsoncake.common.models.ScheduledFlight;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportResponse {

  @JsonProperty("arrivals_count")
  private int arrivalsCount;

  @JsonProperty("filtered_arrivals_count")
  private int filteredArrivalsCount;

  @JsonProperty("flights")
  private List<ScheduledFlight> flights;
}
