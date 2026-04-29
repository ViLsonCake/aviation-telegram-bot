package project.vilsoncake.common.clients.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class SendMediaGroupRequest implements TelegramRequest {

  @JsonProperty("chat_id")
  private final long chatId;

  @JsonProperty("media")
  private final List<InputMediaPhoto> media;

  public SendMediaGroupRequest(long chatId, List<InputMediaPhoto> media) {
    this.chatId = chatId;
    this.media = media;
  }
}
