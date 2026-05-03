package project.vilsoncake.common.repositories;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import project.vilsoncake.common.entities.AircraftFamilyEntity;
import project.vilsoncake.common.entities.UserAircraftFamilyFilterEntity;
import project.vilsoncake.common.entities.UserEntity;

@RequiredArgsConstructor(staticName = "create")
public class UserAircraftFamilyFilterDatabaseProvider {

  private final UserAircraftFamilyFilterRepository filterRepository;
  private final AircraftFamilyRepository aircraftFamilyRepository;

  public Set<String> getFilterFamilyCodes(UserEntity user) {
    return filterRepository.findAllByUser(user).stream()
        .map(filter -> filter.getAircraftFamily().getCode())
        .collect(Collectors.toSet());
  }

  public long countSelectedFamilies(UserEntity user) {
    return filterRepository.countByUser(user);
  }

  public boolean isFamilySelected(UserEntity user, String familyCode) {
    return filterRepository.existsByUserAndAircraftFamily_Code(user, familyCode);
  }

  public void addFamily(UserEntity user, String familyCode) {
    Optional<AircraftFamilyEntity> family = aircraftFamilyRepository.findById(familyCode);
    if (family.isEmpty()) {
      return;
    }
    UserAircraftFamilyFilterEntity filter =
        UserAircraftFamilyFilterEntity.builder()
            .withUser(user)
            .withAircraftFamily(family.get())
            .build();
    filterRepository.save(filter);
  }

  public void removeFamily(UserEntity user, String familyCode) {
    filterRepository.deleteByUserAndFamilyCode(user, familyCode);
  }

  public void clearFilter(UserEntity user) {
    filterRepository.deleteAllByUser(user);
  }
}
