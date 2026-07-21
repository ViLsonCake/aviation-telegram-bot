package project.vilsoncake.flightsnotificationlambda.services;

import static project.vilsoncake.common.utils.BotMessagesUtils.escapeMarkdownV2;
import static project.vilsoncake.common.utils.BotMessagesUtils.formatOriginAirport;
import static project.vilsoncake.common.utils.BotMessagesUtils.formatTimeWithDay;
import static project.vilsoncake.common.utils.BotMessagesUtils.getValueOrUnknown;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.ScheduledFlightEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.models.AirportResponse;
import project.vilsoncake.common.models.ScheduledFlight;
import project.vilsoncake.common.models.ScheduledFlightNotificationFlags;
import project.vilsoncake.common.repositories.ScheduledFlightDatabaseProvider;
import project.vilsoncake.common.repositories.ScheduledFlightNotificationDatabaseProvider;
import project.vilsoncake.flightsnotificationlambda.models.MessageType;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class ScheduledFlightsNotificationSender {

  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;
  private final AircraftNameResolver aircraftNameResolver;
  private final ScheduledFlightDatabaseProvider scheduledFlightDatabaseProvider;
  private final ScheduledFlightNotificationDatabaseProvider
      scheduledFlightNotificationDatabaseProvider;
  private final ScheduledFlightNotificationFlagsResolver scheduledFlightNotificationFlagsResolver;

  // DO NOT UPDATE: increasing the number of flights per message will disable formatting due to
  // Telegram entities limitations
  private static final int MAX_FLIGHTS_PER_MESSAGE = 10;

  private static final String NEW_LINE = "\n";

  public void notifyScheduledFlights(UserEntity user, AirportResponse airportResponse) {
    List<ScheduledFlight> scheduledFlights = airportResponse.getFlights();
    List<ScheduledFlight> unnotifiedScheduledFlights =
        getUnnotifiedScheduledFlights(scheduledFlights, user);

    if (unnotifiedScheduledFlights.isEmpty()) {
      return;
    }

    String timezone = user.getAirport().getTimezone();

    MessageType titleMessageType =
        unnotifiedScheduledFlights.size() > 1
            ? MessageType.SCHEDULED_FLIGHTS_TITLE
            : MessageType.SINGLE_SCHEDULED_FLIGHT_TITLE;

    String notificationTitle = botTemplatesResolver.getTemplate(titleMessageType);

    if (titleMessageType.equals(MessageType.SCHEDULED_FLIGHTS_TITLE)) {
      notificationTitle = String.format(notificationTitle, unnotifiedScheduledFlights.size());
    }

    List<String> messages = new ArrayList<>();
    StringBuilder messageBuilder = new StringBuilder(notificationTitle);
    int flightsInCurrentMessage = 0;

    int counter = 0;

    for (ScheduledFlight scheduledFlight : unnotifiedScheduledFlights) {
      counter++;

      String scheduledFlightDetails;
      if (titleMessageType.equals(MessageType.SINGLE_SCHEDULED_FLIGHT_TITLE)) {
        String scheduledFlightTemplate =
            botTemplatesResolver.getTemplate(MessageType.SINGE_SCHEDULED_FLIGHT_DETAILS);
        scheduledFlightDetails =
            buildScheduledFlightMessage(scheduledFlight, scheduledFlightTemplate, timezone);
      } else {
        String scheduledFlightTemplate =
            botTemplatesResolver.getTemplate(MessageType.PLURAL_SCHEDULED_FLIGHT_DETAILS);
        scheduledFlightDetails =
            buildScheduledFlightMessage(
                counter, scheduledFlight, scheduledFlightTemplate, timezone);
      }

      if (flightsInCurrentMessage >= MAX_FLIGHTS_PER_MESSAGE) {
        messages.add(messageBuilder.toString());
        messageBuilder = new StringBuilder();
        flightsInCurrentMessage = 0;
      }

      messageBuilder.append(NEW_LINE).append(NEW_LINE).append(scheduledFlightDetails);
      flightsInCurrentMessage++;
    }

    String scheduledFlightEndingTemplate =
        botTemplatesResolver.getTemplate(MessageType.SCHEDULED_FLIGHTS_ENDING);
    // NOTE: Should be IATA code only, ICAO code will also work, however, will open the browser
    // version
    // instead of app
    String airportCodeForLink = user.getAirport().getIata();
    String scheduledFlightEnding = String.format(scheduledFlightEndingTemplate, airportCodeForLink);
    messageBuilder.append(NEW_LINE).append(NEW_LINE).append(scheduledFlightEnding);
    messages.add(messageBuilder.toString());

    saveNotifications(unnotifiedScheduledFlights, user);

    telegramClient.sendMessages(user.getChatId(), messages);
  }

  private void saveNotifications(List<ScheduledFlight> scheduledFlights, UserEntity user) {
    for (ScheduledFlight scheduledFlight : scheduledFlights) {
      try {
        ScheduledFlightEntity entity =
            scheduledFlightDatabaseProvider.getOrCreate(scheduledFlight, user.getAirport());
        ScheduledFlightNotificationFlags flags =
            scheduledFlightNotificationFlagsResolver.resolve(entity);
        scheduledFlightNotificationDatabaseProvider.saveNotification(entity, user, flags);
      } catch (Exception e) {
        log.error(
            "Failed to save notification for flight {}, skipping",
            scheduledFlight.getRowId(),
            e);
      }
    }

    log.info("All {} unnotified scheduled flights saved", scheduledFlights.size());
  }

  private String buildScheduledFlightMessage(
      ScheduledFlight scheduledFlight, String scheduledFlightTemplate, String timezone) {
    String aircraftName =
        aircraftNameResolver.getAircraftName(
            scheduledFlight.getAircraftCode(), scheduledFlight.getAircraftName());
    ZonedDateTime arrivalDateTime =
        Instant.ofEpochSecond(scheduledFlight.getScheduledArrivalTime())
            .atZone(ZoneId.of(timezone));
    String formattedScheduledArrivalTime = formatTimeWithDay(arrivalDateTime);
    return String.format(
        scheduledFlightTemplate,
        escapeMarkdownV2(aircraftName),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getCallsign())),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getRegistration())),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getAirlineName())),
        escapeMarkdownV2(
            formatOriginAirport(
                scheduledFlight.getOriginAirportName(), scheduledFlight.getOriginAirportIata())),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getStatus())),
        escapeMarkdownV2(getValueOrUnknown(formattedScheduledArrivalTime)));
  }

  private String buildScheduledFlightMessage(
      int listNumber,
      ScheduledFlight scheduledFlight,
      String scheduledFlightTemplate,
      String timezone) {
    String aircraftName =
        aircraftNameResolver.getAircraftName(
            scheduledFlight.getAircraftCode(), scheduledFlight.getAircraftName());
    ZonedDateTime arrivalDateTime =
        Instant.ofEpochSecond(scheduledFlight.getScheduledArrivalTime())
            .atZone(ZoneId.of(timezone));
    String formattedScheduledArrivalTime = formatTimeWithDay(arrivalDateTime);
    return String.format(
        scheduledFlightTemplate,
        listNumber,
        escapeMarkdownV2(aircraftName),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getCallsign())),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getRegistration())),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getAirlineName())),
        escapeMarkdownV2(
            formatOriginAirport(
                scheduledFlight.getOriginAirportName(), scheduledFlight.getOriginAirportIata())),
        escapeMarkdownV2(getValueOrUnknown(scheduledFlight.getStatus())),
        escapeMarkdownV2(getValueOrUnknown(formattedScheduledArrivalTime)));
  }

  private List<ScheduledFlight> getUnnotifiedScheduledFlights(
      List<ScheduledFlight> flights, UserEntity user) {
    return flights.stream()
        .filter(
            flight ->
                !scheduledFlightNotificationDatabaseProvider.isNotificationSent(
                    ScheduledFlightEntity.constructId(
                        flight.getRowId(), flight.getScheduledDepartureTime()),
                    user))
        .toList();
  }
}
