package project.vilsoncake.common.messages;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.models.CallbackType;
import project.vilsoncake.common.models.UserStateResponseTemplate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotTemplates {
  Map<BotCommand, String> commands;
  Map<UserState, Map<UserStateResponseTemplate, String>> userStateResponses;
  Map<CallbackType, CallbackTemplate> callbacks;
}
