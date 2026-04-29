package project.vilsoncake.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledFlight {

  /**
   * Internal generic FR24 ID assigned only to the live flights, could be null, required to create a
   * link to the live flight.
   */
  @JsonProperty("id")
  private String id;

  /** Internal row FR24 ID, always present for flight. */
  @JsonProperty("row_id")
  private String rowId;

  @JsonProperty("aircraft_code")
  private String aircraftCode;

  @JsonProperty("aircraft_name")
  private String aircraftName;

  @JsonProperty("airline_name")
  private String airlineName;

  @JsonProperty("origin_airport_name")
  private String originAirportName;

  @JsonProperty("origin_airport_iata")
  private String originAirportIata;

  @JsonProperty("origin_airport_icao")
  private String originAirportIcao;

  @JsonProperty("callsign")
  private String callsign;

  @JsonProperty("registration")
  private String registration;

  @JsonProperty("live")
  private Boolean live;

  @JsonProperty("status")
  private String status;

  @JsonProperty("diverted")
  private String diverted;

  @JsonProperty("scheduled_departure_time")
  private Integer scheduledDepartureTime;

  @JsonProperty("scheduled_arrival_time")
  private Integer scheduledArrivalTime;

  @JsonProperty("estimated_arrival_time")
  private Integer estimatedArrivalTime;

  @JsonProperty("images")
  private List<FlightImage> images;
}
