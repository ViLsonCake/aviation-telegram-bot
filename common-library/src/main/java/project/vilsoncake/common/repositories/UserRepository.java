package project.vilsoncake.common.repositories;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.vilsoncake.common.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByUsername(String username);

  @Query(
      value = "SELECT * FROM users WHERE state = 'ALL_SET' AND bot_mode IN (:modes)",
      nativeQuery = true)
  Set<UserEntity> findEligibleUsersToSendNotificationsByMode(@Param("modes") Set<String> modes);
}
