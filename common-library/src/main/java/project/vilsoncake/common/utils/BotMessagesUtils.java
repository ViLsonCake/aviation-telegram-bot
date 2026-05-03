package project.vilsoncake.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BotMessagesUtils {
  public static String getValueOrUnknown(String value) {
    return value == null || value.isBlank() ? "Unknown" : value;
  }

  public static String formatOriginAirport(String name, String icao) {
    String resolvedName = getValueOrUnknown(name);
    String resolvedIcao = getValueOrUnknown(icao);
    return resolvedName + " (" + resolvedIcao + ")";
  }

  public static String resolveEtaLabel(
      Integer etaEpoch, Integer scheduledArrivalEpoch, ZoneId airportZone) {
    Integer timeToShow = etaEpoch != null ? etaEpoch : scheduledArrivalEpoch;
    if (timeToShow == null) {
      return "Unknown";
    }
    ZonedDateTime eta = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timeToShow), airportZone);
    return formatTimeWithDay(eta);
  }

  public static String formatDuration(long minutes) {
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

  public static String formatTimeWithDay(ZonedDateTime date) {
    LocalDate today = LocalDate.now(date.getZone());
    String time = date.format(DateTimeFormatter.ofPattern("HH:mm"));
    String dayLabel = date.toLocalDate().equals(today) ? "today" : "tomorrow";
    return time + " (" + dayLabel + ")";
  }
}
