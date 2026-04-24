package project.vilsoncake.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SPECIFIC_AIRCRAFT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(setterPrefix = "with")
public class SpecificAircraftFlightNotificationEntity extends FlightNotificationEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "specific_aircraft_flight_id")
  private SpecificAircraftFlightEntity specificAircraftFlight;

  @Column(name = "took_off")
  private Boolean tookOff;

  @Column(name = "on_ground")
  private Boolean onGround;

  @Column(name = "landing")
  private Boolean landing;

  @Column(name = "flying_near_airport")
  private Boolean flyingNearAirport;

  @Column(name = "at_user_airport")
  private Boolean atUserAirport;
}
