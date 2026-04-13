package project.vilsoncake.common.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.vilsoncake.common.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByUsername(String username);
}
