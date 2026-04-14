package project.vilsoncake.common.repositories;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.vilsoncake.common.configurations.GeneralConfig;
import project.vilsoncake.common.entities.AirportEntity;
import project.vilsoncake.common.entities.UserEntity;
import project.vilsoncake.common.entities.enums.UserState;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class UserDatabaseProvider {

  private final UserRepository userRepository;
  private final GeneralConfig generalConfig;

  public void create(String username, Long chatId) {
    UserEntity user =
        UserEntity.builder()
            .withUsername(username)
            .withChatId(chatId)
            .withState(UserState.CHOOSING_AIRPORT)
            .withCreatedAt(ZonedDateTime.now(ZoneId.of(generalConfig.getTimezone())))
            .withUpdatedAt(ZonedDateTime.now(ZoneId.of(generalConfig.getTimezone())))
            .build();

    userRepository.save(user);

    log.info("User with username '{}' and chat ID '{}' created successfully", username, chatId);
  }

  public void updateAirportAndState(
      UserEntity userEntity, AirportEntity airportEntity, UserState userState) {
    userEntity.setAirport(airportEntity);
    userEntity.setState(userState);
    userEntity.setUpdatedAt(ZonedDateTime.now(ZoneId.of(generalConfig.getTimezone())));
    userRepository.save(userEntity);

    log.info(
        "User state updated to '{}' for user with username '{}'",
        userState,
        userEntity.getUsername());
  }

  public void updateState(UserEntity userEntity, UserState userState) {
    userEntity.setState(userState);
    userEntity.setUpdatedAt(ZonedDateTime.now(ZoneId.of(generalConfig.getTimezone())));
    userRepository.save(userEntity);
  }

  public Optional<UserEntity> getByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
