package project.vilsoncake.flightsnotificationlambda.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.clients.TelegramClient;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.flightsnotificationlambda.models.AirportResponse;
import project.vilsoncake.flightsnotificationlambda.models.MessageType;
import project.vilsoncake.flightsnotificationlambda.models.ScheduledFlight;

@RequiredArgsConstructor(staticName = "create")
public class UserNotificationsService {

  private final TelegramClient telegramClient;
  private final BotTemplatesResolver botTemplatesResolver;
  private final AircraftNameResolver aircraftNameResolver;

  // DO NOT UPDATE: increasing the number of flights per message will disable formatting due to
  // Telegram entities limitations
  private static final int MAX_FLIGHTS_PER_MESSAGE = 10;

  private static final String NEW_LINE = "\n";

  public void notifyScheduledFlights(UserEntity user, AirportResponse airportResponse) {
    MessageType titleMessageType =
        airportResponse.getFilteredArrivalsCount() > 1
            ? MessageType.SCHEDULED_FLIGHTS_TITLE
            : MessageType.SINGLE_SCHEDULED_FLIGHT_TITLE;

    String notificationTitle = botTemplatesResolver.getTemplate(titleMessageType);

    if (titleMessageType.equals(MessageType.SCHEDULED_FLIGHTS_TITLE)) {
      notificationTitle =
          String.format(notificationTitle, airportResponse.getFilteredArrivalsCount());
    }

    List<ScheduledFlight> scheduledFlights = airportResponse.getFlights();
    String timezone = user.getAirport().getTimezone();
    List<String> messages = new ArrayList<>();
    StringBuilder messageBuilder = new StringBuilder(notificationTitle);
    int flightsInCurrentMessage = 0;

    int counter = 0;

    for (ScheduledFlight scheduledFlight : scheduledFlights) {
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

    messages.add(messageBuilder.toString());

    telegramClient.sendMessages(user.getChatId(), messages);
  }

  private String buildScheduledFlightMessage(
      ScheduledFlight scheduledFlight, String scheduledFlightTemplate, String timezone) {
    String aircraftName =
        aircraftNameResolver.getAircraftName(
            scheduledFlight.getAircraftCode(), scheduledFlight.getAircraftName());
    String formattedScheduledArrivalTime =
        Instant.ofEpochSecond(scheduledFlight.getScheduledArrivalTime())
            .atZone(ZoneId.of(timezone))
            .format(DateTimeFormatter.ofPattern("HH:mm"));
    return String.format(
        scheduledFlightTemplate,
        aircraftName,
        getValueOrUnknown(scheduledFlight.getCallsign()),
        getValueOrUnknown(scheduledFlight.getRegistration()),
        getValueOrUnknown(scheduledFlight.getAirlineName()),
        getValueOrUnknown(scheduledFlight.getOriginAirportName()),
        getValueOrUnknown(scheduledFlight.getStatus()),
        getValueOrUnknown(formattedScheduledArrivalTime));
  }

  private String buildScheduledFlightMessage(
      int listNumber,
      ScheduledFlight scheduledFlight,
      String scheduledFlightTemplate,
      String timezone) {
    String aircraftName =
        aircraftNameResolver.getAircraftName(
            scheduledFlight.getAircraftCode(), scheduledFlight.getAircraftName());
    String formattedScheduledArrivalTime =
        Instant.ofEpochSecond(scheduledFlight.getScheduledArrivalTime())
            .atZone(ZoneId.of(timezone))
            .format(DateTimeFormatter.ofPattern("HH:mm"));
    return String.format(
        scheduledFlightTemplate,
        listNumber,
        aircraftName,
        getValueOrUnknown(scheduledFlight.getCallsign()),
        getValueOrUnknown(scheduledFlight.getRegistration()),
        getValueOrUnknown(scheduledFlight.getAirlineName()),
        getValueOrUnknown(scheduledFlight.getOriginAirportName()),
        getValueOrUnknown(scheduledFlight.getStatus()),
        getValueOrUnknown(formattedScheduledArrivalTime));
  }

  private String getValueOrUnknown(String value) {
    return value != null && !value.isBlank() ? value : "Unknown";
  }
}
