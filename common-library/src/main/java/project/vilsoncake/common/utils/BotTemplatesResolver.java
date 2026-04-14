package project.vilsoncake.common.utils;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.exceptions.TemplateNotFound;
import project.vilsoncake.common.messages.BotTemplates;
import project.vilsoncake.common.models.BotCommand;
import project.vilsoncake.common.models.UserStateResponseTemplate;

@RequiredArgsConstructor(staticName = "create")
public class BotTemplatesResolver {

  private final BotTemplates botTemplates;

  public String getTemplate(BotCommand botCommand) {
    Map<BotCommand, String> commandsTemplates = botTemplates.getCommands();

    if (!commandsTemplates.containsKey(botCommand)) {
      throw new TemplateNotFound("Message template not found for bot command: " + botCommand);
    }

    return commandsTemplates.get(botCommand);
  }

  public String getTemplate(
      UserState userState, UserStateResponseTemplate userStateResponseTemplate) {
    Map<UserState, Map<UserStateResponseTemplate, String>> userStateResponses =
        botTemplates.getUserStateResponses();

    if (!userStateResponses.containsKey(userState)) {
      throw new TemplateNotFound("Message template not found for user state: " + userState);
    }

    Map<UserStateResponseTemplate, String> userStateResponseTemplates =
        userStateResponses.get(userState);

    if (!userStateResponseTemplates.containsKey(userStateResponseTemplate)) {
      throw new TemplateNotFound(
          "Message template not found for user state response: " + userStateResponseTemplate);
    }

    return userStateResponseTemplates.get(userStateResponseTemplate);
  }
}
