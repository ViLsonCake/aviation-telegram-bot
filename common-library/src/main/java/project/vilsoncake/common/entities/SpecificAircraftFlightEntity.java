package project.vilsoncake.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "specific_aircraft_flights")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class SpecificAircraftFlightEntity {

  @Id
  @Setter(lombok.AccessLevel.NONE)
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aircraft_code", nullable = false)
  private WideBodyAircraftEntity aircraft;

  @Column(name = "airline_name", nullable = false)
  private String airlineName;

  @Column(name = "callsign", nullable = false)
  private String callsign;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "origin_airport_icao", referencedColumnName = "icao", nullable = false)
  private AirportEntity originAirport;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_airport_icao", referencedColumnName = "icao", nullable = false)
  private AirportEntity destinationAirport;

  @Column(name = "altitude", nullable = false)
  private Integer altitude;

  @Column(name = "ground_speed", nullable = false)
  private Integer groundSpeed;

  @Column(name = "vertical_speed", nullable = false)
  private Integer verticalSpeed;

  @Column(name = "heading", nullable = false)
  private Integer heading;

  @Column(name = "latitude", nullable = false)
  private Double latitude;

  @Column(name = "longitude", nullable = false)
  private Double longitude;
}
