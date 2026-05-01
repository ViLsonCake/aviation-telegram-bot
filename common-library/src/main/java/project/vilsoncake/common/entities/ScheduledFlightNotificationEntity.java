package project.vilsoncake.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SCHEDULED_FLIGHTS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(setterPrefix = "with")
public class ScheduledFlightNotificationEntity extends FlightNotificationEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scheduled_flight_id")
  private ScheduledFlightEntity scheduledFlight;

  @Column(name = "notified_delayed")
  private Boolean notifiedDelayed;

  @Column(name = "notified_cancelled")
  private Boolean notifiedCancelled;

  @Column(name = "notified_diverted")
  private Boolean notifiedDiverted;

  @Column(name = "notified_arriving_soon")
  private Boolean notifiedArrivingSoon;

  @Column(name = "notified_live")
  private Boolean notifiedLive;

  @Column(name = "notified_landed")
  private Boolean notifiedLanded;

  @Column(name = "last_notified_eta")
  private ZonedDateTime lastNotifiedEstimatedArrivalTime;
}
