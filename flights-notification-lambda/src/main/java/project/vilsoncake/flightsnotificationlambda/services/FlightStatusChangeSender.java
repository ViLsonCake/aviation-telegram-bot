package project.vilsoncake.flightsnotificationlambda.services;

import static project.vilsoncake.common.utils.BotMessagesUtils.getValueOrUnknown;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
public class FlightStatusChangeSender {

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

    if (!Boolean.TRUE.equals(notificationEntity.getNotifiedLive()) && currentFlight.getLive()) {
      String eta = resolveEtaLabel(currentFlight.getEstimatedArrivalTime(), airportZone);
      String message =
          botTemplatesResolver.getTemplate(MessageType.FLIGHT_LIVE_NOTIFICATION)
              + "\n\n"
              + String.format(
                  botTemplatesResolver.getTemplate(MessageType.FLIGHT_LIVE_NOTIFICATION_DETAILS),
                  aircraftName,
                  getValueOrUnknown(currentFlight.getCallsign()),
                  getValueOrUnknown(currentFlight.getRegistration()),
                  getValueOrUnknown(currentFlight.getAirlineName()),
                  getValueOrUnknown(currentFlight.getOriginAirportName()),
                  eta);
      telegramClient.sendMessages(chatId, List.of(message));
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

    if (Boolean.FALSE.equals(notificationEntity.getNotifiedDelayed())
        && currentFlight.getStatus().startsWith(FlightStatus.DELAYED.getFlightradarName())) {
      String eta = resolveEtaLabel(currentFlight.getEstimatedArrivalTime(), airportZone);
      String message =
          botTemplatesResolver.getTemplate(MessageType.FLIGHT_DELAYED_NOTIFICATION)
              + "\n\n"
              + String.format(
                  botTemplatesResolver.getTemplate(MessageType.FLIGHT_DELAYED_NOTIFICATION_DETAILS),
                  aircraftName,
                  getValueOrUnknown(currentFlight.getCallsign()),
                  getValueOrUnknown(currentFlight.getAirlineName()),
                  eta);
      telegramClient.sendMessages(chatId, List.of(message));
      notificationEntity.setNotifiedDelayed(true);
      changed = true;
      log.info(
          "Sent delayed notification for flight {} to user {}",
          scheduledFlightEntity.getRowId(),
          notificationEntity.getUser().getUsername());
    }

    if (Boolean.FALSE.equals(notificationEntity.getNotifiedCancelled())
        && currentFlight.getStatus().startsWith(FlightStatus.CANCELLED.getFlightradarName())) {
      String message =
          botTemplatesResolver.getTemplate(MessageType.FLIGHT_CANCELLED_NOTIFICATION)
              + "\n\n"
              + String.format(
                  botTemplatesResolver.getTemplate(
                      MessageType.FLIGHT_CANCELLED_NOTIFICATION_DETAILS),
                  aircraftName,
                  getValueOrUnknown(currentFlight.getCallsign()),
                  getValueOrUnknown(currentFlight.getAirlineName()));
      telegramClient.sendMessages(chatId, List.of(message));
      notificationEntity.setNotifiedCancelled(true);
      changed = true;
      log.info(
          "Sent cancelled notification for flight {} to user {}",
          scheduledFlightEntity.getRowId(),
          notificationEntity.getUser().getUsername());
    }

    if (currentFlight.getLive() && currentFlight.getEstimatedArrivalTime() != null) {
      ZonedDateTime currentEta =
          ZonedDateTime.ofInstant(
              Instant.ofEpochSecond(currentFlight.getEstimatedArrivalTime()), airportZone);
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
                    formatEtaWithDay(currentEta));
        telegramClient.sendMessages(chatId, List.of(message));
        notificationEntity.setLastNotifiedEstimatedArrivalTime(currentEta);
        changed = true;
        log.info(
            "Sent ETA {} notification for flight {} to user {}",
            isDelayed ? "delayed" : "earlier",
            scheduledFlightEntity.getRowId(),
            notificationEntity.getUser().getUsername());
      }
    }

    if (Boolean.FALSE.equals(notificationEntity.getNotifiedArrivingSoon())
        && currentFlight.getLive()
        && currentFlight.getEstimatedArrivalTime() != null) {
      ZonedDateTime currentEta =
          ZonedDateTime.ofInstant(
              Instant.ofEpochSecond(currentFlight.getEstimatedArrivalTime()), airportZone);
      long minutesUntilArrival =
          ChronoUnit.MINUTES.between(ZonedDateTime.now(airportZone), currentEta);

      if (minutesUntilArrival >= 0
          && minutesUntilArrival <= notificationsConfig.getArrivingSoonRemainingMinutes()) {
        String message =
            botTemplatesResolver.getTemplate(MessageType.FLIGHT_ARRIVING_SOON_NOTIFICATION)
                + "\n\n"
                + String.format(
                    botTemplatesResolver.getTemplate(
                        MessageType.FLIGHT_ARRIVING_SOON_NOTIFICATION_DETAILS),
                    aircraftName,
                    getValueOrUnknown(currentFlight.getCallsign()),
                    getValueOrUnknown(currentFlight.getAirlineName()),
                    formatEtaWithDay(currentEta));
        telegramClient.sendMessages(chatId, List.of(message));
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

  private String resolveEtaLabel(Integer etaEpoch, ZoneId airportZone) {
    if (etaEpoch == null) {
      return "Unknown";
    }
    ZonedDateTime eta = ZonedDateTime.ofInstant(Instant.ofEpochSecond(etaEpoch), airportZone);
    return formatEtaWithDay(eta);
  }

  private String formatDuration(long minutes) {
    if (minutes < 60) {
      return minutes + " minute" + (minutes == 1 ? "" : "s");
    }
    long hours = minutes / 60;
    long remainingMinutes = minutes % 60;
    String hoursPart = hours + " hour" + (hours == 1 ? "" : "s");
    if (remainingMinutes == 0) {
      return hoursPart;
    }
    return hoursPart + " " + remainingMinutes + " minute" + (remainingMinutes == 1 ? "" : "s");
  }

  private String formatEtaWithDay(ZonedDateTime eta) {
    LocalDate today = LocalDate.now(eta.getZone());
    String time = eta.format(DateTimeFormatter.ofPattern("HH:mm"));
    String dayLabel = eta.toLocalDate().equals(today) ? "today" : "tomorrow";
    return time + " (" + dayLabel + ")";
  }
}
