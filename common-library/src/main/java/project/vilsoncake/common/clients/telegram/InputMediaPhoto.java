package project.vilsoncake.common.clients.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class InputMediaPhoto {

  @JsonProperty("type")
  private final String type = "photo";

  @JsonProperty("media")
  private final String media;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("caption")
  private final String caption;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("parse_mode")
  private final String parseMode;

  public InputMediaPhoto(String media) {
    this.media = media;
    this.caption = null;
    this.parseMode = null;
  }

  public InputMediaPhoto(String media, String caption) {
    this.media = media;
    this.caption = caption;
    this.parseMode = TelegramRequest.DEFAULT_PARSE_MODE;
  }
}
