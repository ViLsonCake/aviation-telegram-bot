package project.vilsoncake.common.clients.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerCallbackQueryRequest implements TelegramRequest {

  @JsonProperty("callback_query_id")
  private final String callbackQueryId;

  @JsonProperty("text")
  private final String text;

  @JsonProperty("show_alert")
  private final Boolean showAlert;
}
