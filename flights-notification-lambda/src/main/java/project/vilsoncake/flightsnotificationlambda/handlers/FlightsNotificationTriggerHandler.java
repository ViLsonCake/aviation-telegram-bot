package project.vilsoncake.flightsnotificationlambda.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationTriggerPayload;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationType;
import project.vilsoncake.flightsnotificationlambda.services.adapters.NotificationTypeAdapter;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class FlightsNotificationTriggerHandler {

  private final NotificationTypeAdapter notificationTypeAdapter;

  public String handle(FlightsNotificationTriggerPayload flightsNotificationTriggerPayload) {
    try {
      FlightsNotificationType type = flightsNotificationTriggerPayload.getType();

      notificationTypeAdapter.process(type);
    } catch (Exception e) {
      log.error("Error occurred while processing notification type", e);
      return "ERROR";
    }

    return "OK";
  }
}
