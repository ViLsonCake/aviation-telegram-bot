package project.vilsoncake.flightsnotificationlambda.processors;

import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationType;

public interface NotificationTypeProcessor {
  FlightsNotificationType getNotificationType();

  void process();
}
