package project.vilsoncake.flightsnotificationlambda.handlers;

import lombok.RequiredArgsConstructor;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationTriggerPayload;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationType;
import project.vilsoncake.flightsnotificationlambda.services.adapters.NotificationTypeAdapter;

@RequiredArgsConstructor(staticName = "create")
public class FlightsNotificationTriggerHandler {

  private final NotificationTypeAdapter notificationTypeAdapter;

  public String handle(FlightsNotificationTriggerPayload flightsNotificationTriggerPayload) {
    try {
      FlightsNotificationType type = flightsNotificationTriggerPayload.getType();

      notificationTypeAdapter.process(type);
    } catch (Exception e) {
      return "ERROR";
    }

    return "OK";
  }
}
