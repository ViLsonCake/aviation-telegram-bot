package project.vilsoncake.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
