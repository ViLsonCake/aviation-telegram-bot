package project.vilsoncake.flightsnotificationlambda.services.adapters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import project.vilsoncake.flightsnotificationlambda.models.FlightsNotificationType;
import project.vilsoncake.flightsnotificationlambda.processors.NotificationTypeProcessor;

public class NotificationTypeAdapter {

  private final Map<FlightsNotificationType, NotificationTypeProcessor> notificationTypeProcessors;

  public static NotificationTypeAdapter create(
      List<NotificationTypeProcessor> notificationTypeProcessors) {
    return new NotificationTypeAdapter(notificationTypeProcessors);
  }

  private NotificationTypeAdapter(List<NotificationTypeProcessor> notificationTypeProcessors) {
    this.notificationTypeProcessors =
        notificationTypeProcessors.stream()
            .collect(
                LinkedHashMap::new,
                (map, processor) -> map.put(processor.getNotificationType(), processor),
                Map::putAll);
  }

  public void process(FlightsNotificationType flightsNotificationType) {
    NotificationTypeProcessor notificationTypeProcessor =
        notificationTypeProcessors.get(flightsNotificationType);

    // Silently ignore unknown notification types
    if (notificationTypeProcessor == null) {
      return;
    }

    notificationTypeProcessor.process();
  }
}
