package project.vilsoncake.flightsnotificationlambda.models;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotTemplates {
  Map<MessageType, String> notifications;
}
