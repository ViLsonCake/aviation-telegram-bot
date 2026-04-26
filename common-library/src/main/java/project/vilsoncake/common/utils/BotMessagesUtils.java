package project.vilsoncake.common.utils;

public class BotMessagesUtils {
  public static String getValueOrUnknown(String value) {
    return value == null || value.isBlank() ? "Unknown" : value;
  }
}
