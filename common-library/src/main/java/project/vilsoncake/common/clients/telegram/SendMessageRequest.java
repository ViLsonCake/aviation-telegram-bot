package project.vilsoncake.common.clients.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/** Represents the JSON body of a Telegram sendMessage API request. */
@Getter
public class SendMessageRequest implements TelegramRequest {

  @JsonProperty("chat_id")
  private final long chatId;

  @JsonProperty("text")
  private final String text;

  @JsonProperty("parse_mode")
  private final String parseMode;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("reply_markup")
  private final InlineKeyboardMarkup replyMarkup;

  private SendMessageRequest(
      long chatId, String text, String parseMode, InlineKeyboardMarkup replyMarkup) {
    this.chatId = chatId;
    this.text = text;
    this.parseMode = parseMode;
    this.replyMarkup = replyMarkup;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private long chatId;
    private String text;
    private InlineKeyboardMarkup replyMarkup;

    private Builder() {}

    public Builder withChatId(long chatId) {
      this.chatId = chatId;
      return this;
    }

    public Builder withText(String text) {
      this.text = text;
      return this;
    }

    public Builder withReplyMarkup(InlineKeyboardMarkup replyMarkup) {
      this.replyMarkup = replyMarkup;
      return this;
    }

    public SendMessageRequest build() {
      return new SendMessageRequest(chatId, text, DEFAULT_PARSE_MODE, replyMarkup);
    }
  }
}
