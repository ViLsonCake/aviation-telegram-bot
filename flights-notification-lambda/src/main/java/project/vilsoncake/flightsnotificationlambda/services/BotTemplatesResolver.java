package project.vilsoncake.flightsnotificationlambda.services;

import lombok.RequiredArgsConstructor;
import project.vilsoncake.flightsnotificationlambda.models.BotTemplates;
import project.vilsoncake.flightsnotificationlambda.models.MessageType;

@RequiredArgsConstructor(staticName = "create")
public class BotTemplatesResolver {

  private final BotTemplates botTemplates;

  public String getTemplate(MessageType messageType) {
    return botTemplates.getNotifications().get(messageType);
  }
}
