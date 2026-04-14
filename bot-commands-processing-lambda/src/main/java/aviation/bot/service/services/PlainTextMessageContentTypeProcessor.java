package aviation.bot.service.services;

import aviation.bot.service.models.MessageContentType;
import aviation.bot.service.services.adapters.PlainTextProcessorsAdapter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.UserState;
import project.vilsoncake.common.repositories.UserDatabaseProvider;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class PlainTextMessageContentTypeProcessor implements MessageContentTypeProcessor {

  private final UserDatabaseProvider userDatabaseProvider;
  private final PlainTextProcessorsAdapter plainTextProcessorsAdapter;

  @Override
  public MessageContentType getMessageContentType() {
    return MessageContentType.PLAIN_TEXT;
  }

  @Override
  public void process(String username, long chatId, String text) {
    Optional<UserEntity> userEntity = userDatabaseProvider.getByUsername(username);

    if (userEntity.isEmpty()) {
      return;
    }

    UserState userState = userEntity.get().getState();

    plainTextProcessorsAdapter.process(userState, username, chatId, text);
  }
}
