package project.vilsoncake.common.clients.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/** Represents the inline keyboard attached to a Telegram message. */
@Getter
public class InlineKeyboardMarkup {

  private static final int MAX_ROWS = 100;
  private static final int MAX_BUTTONS_PER_ROW = 8;

  @JsonProperty("inline_keyboard")
  private final List<List<InlineKeyboardButton>> inlineKeyboard;

  private InlineKeyboardMarkup(List<List<InlineKeyboardButton>> inlineKeyboard) {
    this.inlineKeyboard = Collections.unmodifiableList(inlineKeyboard);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

    private Builder() {}

    /**
     * Appends a row of buttons to the keyboard.
     *
     * @param buttons one to eight buttons for this row
     * @throws IllegalArgumentException if the row is empty, exceeds 8 buttons, or the keyboard
     *     already has 100 rows
     */
    public Builder addRow(InlineKeyboardButton... buttons) {
      if (buttons == null || buttons.length == 0) {
        throw new IllegalArgumentException("A keyboard row must contain at least one button");
      }
      if (buttons.length > MAX_BUTTONS_PER_ROW) {
        throw new IllegalArgumentException(
            "A keyboard row must not exceed "
                + MAX_BUTTONS_PER_ROW
                + " buttons, got: "
                + buttons.length);
      }
      if (rows.size() >= MAX_ROWS) {
        throw new IllegalArgumentException(
            "An inline keyboard must not exceed " + MAX_ROWS + " rows");
      }
      rows.add(Collections.unmodifiableList(Arrays.asList(buttons)));
      return this;
    }

    /**
     * Builds the {@code InlineKeyboardMarkup}.
     *
     * @throws IllegalStateException if no rows have been added
     */
    public InlineKeyboardMarkup build() {
      if (rows.isEmpty()) {
        throw new IllegalStateException("An inline keyboard must contain at least one row");
      }
      return new InlineKeyboardMarkup(new ArrayList<>(rows));
    }
  }
}
