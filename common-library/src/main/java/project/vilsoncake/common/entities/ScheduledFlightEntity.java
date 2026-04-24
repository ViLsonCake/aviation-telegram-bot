package project.vilsoncake.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scheduled_flights")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class ScheduledFlightEntity {

  @Id
  @Setter(lombok.AccessLevel.NONE)
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aircraft_code", nullable = false)
  private WideBodyAircraftEntity aircraft;

  @Column(name = "airline_name", nullable = false)
  private String airlineName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "origin_airport_icao", referencedColumnName = "icao", nullable = false)
  private AirportEntity originAirport;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_airport_icao", referencedColumnName = "icao", nullable = false)
  private AirportEntity destinationAirport;

  @Column(name = "callsign", nullable = false)
  private String callsign;

  @Column(name = "registration", nullable = false)
  private String registration;

  @Column(name = "live", nullable = false)
  private Boolean live;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "scheduled_departure_time", nullable = false)
  private ZonedDateTime scheduledDepartureTime;

  @Column(name = "scheduled_arrival_time", nullable = false)
  private ZonedDateTime scheduledArrivalTime;

  @Column(name = "estimated_arrival_time")
  private ZonedDateTime estimatedArrivalTime;
}
