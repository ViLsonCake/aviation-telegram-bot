package project.vilsoncake.flightsnotificationlambda.services;

import static project.vilsoncake.common.utils.BotMessagesUtils.formatDuration;
import static project.vilsoncake.common.utils.BotMessagesUtils.formatOriginAirport;
import static project.vilsoncake.common.utils.BotMessagesUtils.formatTimeWithDay;
import static project.vilsoncake.common.utils.BotMessagesUtils.getValueOrUnknown;
import static project.vilsoncake.common.utils.BotMessagesUtils.resolveEtaLabel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.configurations.NotificationsConfig;
import project.vilsoncake.common.entities.ScheduledFlightEntity;
import project.vilsoncake.common.entities.ScheduledFlightNotificationEntity;
import project.vilsoncake.common.models.FlightStatus;
import project.vilsoncake.common.models.ScheduledFlight;
import project.vilsoncake.common.repositories.ScheduledFlightDatabaseProvider;
import project.vilsoncake.common.repositories.ScheduledFlightNotificationDatabaseProvider;
import project.vilsoncake.flightsnotificationlambda.models.MessageType;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class FlightStatusChangeNotificationSender {

  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;
  private final AircraftNameResolver aircraftNameResolver;
  private final ScheduledFlightNotificationDatabaseProvider
      scheduledFlightNotificationDatabaseProvider;
  private final ScheduledFlightDatabaseProvider scheduledFlightDatabaseProvider;
  private final NotificationsConfig notificationsConfig;

  public void sendStatusChangeNotificationsIfNeeded(
      ScheduledFlightNotificationEntity notificationEntity, ScheduledFlight currentFlight) {
    ScheduledFlightEntity scheduledFlightEntity = notificationEntity.getScheduledFlight();
    ZoneId airportZone = ZoneId.of(scheduledFlightEntity.getDestinationAirport().getTimezone());
    long chatId = notificationEntity.getUser().getChatId();
    String aircraftName =
        aircraftNameResolver.getAircraftName(
            currentFlight.getAircraftCode(), currentFlight.getAircraftName());

    boolean changed = false;

    if (!Boolean.TRUE.equals(notificationEntity.getNotifiedLive())
        && Boolean.TRUE.equals(currentFlight.getLive())) {
      String eta =
          resolveEtaLabel(
              currentFlight.getEstimatedArrivalTime(),
              currentFlight.getScheduledArrivalTime(),
              airportZone);
      String message =
          botTemplatesResolver.getTemplate(MessageType.FLIGHT_LIVE_NOTIFICATION)
              + "\n\n"
              + String.format(
                  botTemplatesResolver.getTemplate(MessageType.FLIGHT_LIVE_NOTIFICATION_DETAILS),
                  aircraftName,
                  getValueOrUnknown(currentFlight.getCallsign()),
                  getValueOrUnknown(currentFlight.getRegistration()),
                  getValueOrUnknown(currentFlight.getAirlineName()),
                  formatOriginAirport(
                      currentFlight.getOriginAirportName(), currentFlight.getOriginAirportIata()),
                  eta,
                  currentFlight.getRegistration(),
                  currentFlight.getCallsign(),
                  currentFlight.getId());
      telegramClient.sendMessageWithImages(chatId, message, currentFlight.getImages());
      notificationEntity.setNotifiedLive(true);
      if (currentFlight.getEstimatedArrivalTime() != null) {
        notificationEntity.setLastNotifiedEstimatedArrivalTime(
            ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(currentFlight.getEstimatedArrivalTime()), airportZone));
      }
      if (currentFlight.getStatus().startsWith(FlightStatus.DELAYED.getFlightradarName())) {
        notificationEntity.setNotifiedDelayed(true);
      }
      changed = true;
      log.info(
          "Sent live notification for flight {} to user {}",
          scheduledFlightEntity.getRowId(),
          notificationEntity.getUser().getUsername());
    }

    if (!Boolean.TRUE.equals(notificationEntity.getNotifiedDelayed())
        && currentFlight.getStatus().startsWith(FlightStatus.DELAYED.getFlightradarName())
        && !Boolean.TRUE.equals(currentFlight.getLive())) {
      String eta =
          resolveEtaLabel(
              currentFlight.getEstimatedArrivalTime(),
              currentFlight.getScheduledArrivalTime(),
              airportZone);
      String message =
          botTemplatesResolver.getTemplate(MessageType.FLIGHT_DELAYED_NOTIFICATION)
              + "\n\n"
              + String.format(
                  botTemplatesResolver.getTemplate(MessageType.FLIGHT_DELAYED_NOTIFICATION_DETAILS),
                  aircraftName,
                  getValueOrUnknown(currentFlight.getCallsign()),
                  getValueOrUnknown(currentFlight.getAirlineName()),
                  formatOriginAirport(
                      currentFlight.getOriginAirportName(), currentFlight.getOriginAirportIata()),
                  eta);
      telegramClient.sendMessages(chatId, List.of(message));
      notificationEntity.setNotifiedDelayed(true);
      changed = true;
      log.info(
          "Sent delayed notification for flight {} to user {}",
          scheduledFlightEntity.getRowId(),
          notificationEntity.getUser().getUsername());
    }

    if (!Boolean.TRUE.equals(notificationEntity.getNotifiedCancelled())
        && currentFlight.getStatus().startsWith(FlightStatus.CANCELLED.getFlightradarName())) {
      String message =
          botTemplatesResolver.getTemplate(MessageType.FLIGHT_CANCELLED_NOTIFICATION)
              + "\n\n"
              + String.format(
                  botTemplatesResolver.getTemplate(
                      MessageType.FLIGHT_CANCELLED_NOTIFICATION_DETAILS),
                  aircraftName,
                  getValueOrUnknown(currentFlight.getCallsign()),
                  getValueOrUnknown(currentFlight.getAirlineName()),
                  formatOriginAirport(
                      currentFlight.getOriginAirportName(), currentFlight.getOriginAirportIata()));
      telegramClient.sendMessages(chatId, List.of(message));
      notificationEntity.setNotifiedCancelled(true);
      changed = true;
      log.info(
          "Sent cancelled notification for flight {} to user {}",
          scheduledFlightEntity.getRowId(),
          notificationEntity.getUser().getUsername());
    }

    if (!Boolean.TRUE.equals(notificationEntity.getNotifiedLanded())
        && currentFlight.getStatus().startsWith(FlightStatus.LANDED.getFlightradarName())) {
      ZonedDateTime scheduledArrivalTime =
          ZonedDateTime.ofInstant(
              Instant.ofEpochSecond(currentFlight.getScheduledArrivalTime()), airportZone);
      ZonedDateTime actualArrival =
          currentFlight.getEstimatedArrivalTime() != null
              ? ZonedDateTime.ofInstant(
                  Instant.ofEpochSecond(currentFlight.getEstimatedArrivalTime()), airportZone)
              : scheduledArrivalTime;
      long minutesSinceLanding =
          Math.max(0, ChronoUnit.MINUTES.between(actualArrival, ZonedDateTime.now(airportZone)));
      String estimatedArrival = formatTimeWithDay(actualArrival);
      String message =
          String.format(
                  botTemplatesResolver.getTemplate(MessageType.FLIGHT_LANDED_NOTIFICATION),
                  formatDuration(minutesSinceLanding))
              + "\n\n"
              + String.format(
                  botTemplatesResolver.getTemplate(MessageType.FLIGHT_LANDED_NOTIFICATION_DETAILS),
                  aircraftName,
                  getValueOrUnknown(currentFlight.getCallsign()),
                  getValueOrUnknown(currentFlight.getAirlineName()),
                  formatOriginAirport(
                      currentFlight.getOriginAirportName(), currentFlight.getOriginAirportIata()),
                  formatTimeWithDay(scheduledArrivalTime),
                  estimatedArrival,
                  currentFlight.getRegistration(),
                  currentFlight.getRegistration(),
                  currentFlight.getId());
      telegramClient.sendMessageWithImages(chatId, message, currentFlight.getImages());
      notificationEntity.setNotifiedLanded(true);
      changed = true;
      log.info(
          "Sent landed notification for flight {} to user {}",
          scheduledFlightEntity.getRowId(),
          notificationEntity.getUser().getUsername());
    }

    if (Boolean.TRUE.equals(currentFlight.getLive())
        && currentFlight.getEstimatedArrivalTime() != null) {
      ZonedDateTime currentEta =
          ZonedDateTime.ofInstant(
              Instant.ofEpochSecond(currentFlight.getEstimatedArrivalTime()), airportZone);
      ZonedDateTime scheduledArrivalTime =
          ZonedDateTime.ofInstant(
              Instant.ofEpochSecond(currentFlight.getScheduledArrivalTime()), airportZone);
      ZonedDateTime reference =
          notificationEntity.getLastNotifiedEstimatedArrivalTime() != null
              ? notificationEntity.getLastNotifiedEstimatedArrivalTime()
              : scheduledFlightEntity.getScheduledArrivalTime();
      long minutesDiff = ChronoUnit.MINUTES.between(reference, currentEta);

      if (Math.abs(minutesDiff) >= notificationsConfig.getEtaChangedThresholdMinutes()) {
        boolean isDelayed = minutesDiff > 0;
        String durationText = formatDuration(Math.abs(minutesDiff));
        MessageType titleType =
            isDelayed
                ? MessageType.FLIGHT_ETA_DELAYED_NOTIFICATION
                : MessageType.FLIGHT_ETA_EARLIER_NOTIFICATION;
        String title = String.format(botTemplatesResolver.getTemplate(titleType), durationText);
        String message =
            title
                + "\n\n"
                + String.format(
                    botTemplatesResolver.getTemplate(
                        MessageType.FLIGHT_ETA_CHANGED_NOTIFICATION_DETAILS),
                    aircraftName,
                    getValueOrUnknown(currentFlight.getCallsign()),
                    formatOriginAirport(
                        currentFlight.getOriginAirportName(), currentFlight.getOriginAirportIata()),
                    formatTimeWithDay(scheduledArrivalTime),
                    formatTimeWithDay(currentEta),
                    currentFlight.getRegistration(),
                    currentFlight.getCallsign(),
                    currentFlight.getId());
        telegramClient.sendMessageWithImages(chatId, message, currentFlight.getImages());
        notificationEntity.setLastNotifiedEstimatedArrivalTime(currentEta);
        changed = true;
        log.info(
            "Sent ETA {} notification for flight {} to user {}",
            isDelayed ? "delayed" : "earlier",
            scheduledFlightEntity.getRowId(),
            notificationEntity.getUser().getUsername());
      }
    }

    if (!Boolean.TRUE.equals(notificationEntity.getNotifiedArrivingSoon())
        && Boolean.TRUE.equals(currentFlight.getLive())
        && currentFlight.getEstimatedArrivalTime() != null) {
      ZonedDateTime currentEta =
          ZonedDateTime.ofInstant(
              Instant.ofEpochSecond(currentFlight.getEstimatedArrivalTime()), airportZone);
      ZonedDateTime scheduledArrivalTime =
          ZonedDateTime.ofInstant(
              Instant.ofEpochSecond(currentFlight.getScheduledArrivalTime()), airportZone);
      long minutesUntilArrival =
          ChronoUnit.MINUTES.between(ZonedDateTime.now(airportZone), currentEta);

      if (minutesUntilArrival >= 0
          && minutesUntilArrival <= notificationsConfig.getArrivingSoonRemainingMinutes()) {
        String message =
            String.format(
                    botTemplatesResolver.getTemplate(MessageType.FLIGHT_ARRIVING_SOON_NOTIFICATION),
                    formatDuration(minutesUntilArrival))
                + "\n\n"
                + String.format(
                    botTemplatesResolver.getTemplate(
                        MessageType.FLIGHT_ARRIVING_SOON_NOTIFICATION_DETAILS),
                    aircraftName,
                    getValueOrUnknown(currentFlight.getCallsign()),
                    getValueOrUnknown(currentFlight.getAirlineName()),
                    formatOriginAirport(
                        currentFlight.getOriginAirportName(), currentFlight.getOriginAirportIata()),
                    formatTimeWithDay(scheduledArrivalTime),
                    formatTimeWithDay(currentEta),
                    currentFlight.getRegistration(),
                    currentFlight.getCallsign(),
                    currentFlight.getId());
        telegramClient.sendMessageWithImages(chatId, message, currentFlight.getImages());
        notificationEntity.setNotifiedArrivingSoon(true);
        changed = true;
        log.info(
            "Sent arriving soon notification for flight {} to user {}",
            scheduledFlightEntity.getRowId(),
            notificationEntity.getUser().getUsername());
      }
    }

    if (changed) {
      scheduledFlightNotificationDatabaseProvider.updateStatusChangeFlags(notificationEntity);
    }

    scheduledFlightDatabaseProvider.updateFlightState(scheduledFlightEntity, currentFlight);
  }
}
