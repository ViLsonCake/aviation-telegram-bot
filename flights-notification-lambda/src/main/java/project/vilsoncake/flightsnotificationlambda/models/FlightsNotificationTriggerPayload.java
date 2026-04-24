package project.vilsoncake.flightsnotificationlambda.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightsNotificationTriggerPayload {
  private FlightsNotificationType type;
}
