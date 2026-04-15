package project.vilsoncake.common.clients.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class AnswerCallbackQueryRequest implements TelegramRequest {

  @JsonProperty("callback_query_id")
  private final String callbackQueryId;

  public AnswerCallbackQueryRequest(String callbackQueryId) {
    this.callbackQueryId = callbackQueryId;
  }
}
