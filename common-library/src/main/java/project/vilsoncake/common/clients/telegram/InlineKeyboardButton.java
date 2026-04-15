package project.vilsoncake.common.clients.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;
import lombok.Getter;

/** Represents a single button in an inline keyboard row. */
@Getter
public class InlineKeyboardButton {

  private static final int MAX_CALLBACK_DATA_BYTES = 64;

  @JsonProperty("text")
  private final String text;

  @JsonProperty("callback_data")
  private final String callbackData;

  private InlineKeyboardButton(String text, String callbackData) {
    this.text = text;
    this.callbackData = callbackData;
  }

  /**
   * Creates an inline keyboard button.
   *
   * @param text label shown on the button
   * @param callbackData data sent back when the user clicks; must be 1–64 bytes (UTF-8)
   * @throws IllegalArgumentException if callbackData is empty or exceeds 64 bytes
   */
  public static InlineKeyboardButton of(String text, String callbackData) {
    if (callbackData == null || callbackData.isEmpty()) {
      throw new IllegalArgumentException("callbackData must not be null or empty");
    }
    if (callbackData.getBytes(StandardCharsets.UTF_8).length > MAX_CALLBACK_DATA_BYTES) {
      throw new IllegalArgumentException(
          "callbackData exceeds the 64-byte Telegram limit: " + callbackData);
    }
    return new InlineKeyboardButton(text, callbackData);
  }
}
