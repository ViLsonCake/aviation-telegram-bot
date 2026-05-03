package project.vilsoncake.common.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import project.vilsoncake.common.entities.UserAircraftFamilyFilterEntity;
import project.vilsoncake.common.entities.UserAircraftFamilyFilterId;
import project.vilsoncake.common.entities.UserEntity;

public interface UserAircraftFamilyFilterRepository
    extends JpaRepository<UserAircraftFamilyFilterEntity, UserAircraftFamilyFilterId> {

  List<UserAircraftFamilyFilterEntity> findAllByUser(UserEntity user);

  long countByUser(UserEntity user);

  boolean existsByUserAndAircraftFamily_Code(UserEntity user, String familyCode);

  @Transactional
  @Modifying
  @Query(
      "DELETE FROM UserAircraftFamilyFilterEntity f WHERE f.user = :user AND f.aircraftFamily.code = :familyCode")
  void deleteByUserAndFamilyCode(
      @Param("user") UserEntity user, @Param("familyCode") String familyCode);

  @Transactional
  @Modifying
  void deleteAllByUser(UserEntity user);
}
