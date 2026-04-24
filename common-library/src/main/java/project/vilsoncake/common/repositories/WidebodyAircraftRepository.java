package project.vilsoncake.common.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.vilsoncake.common.entities.WideBodyAircraftEntity;

public interface WidebodyAircraftRepository extends JpaRepository<WideBodyAircraftEntity, String> {
  @Query(
      "SELECT w FROM WideBodyAircraftEntity w JOIN FETCH w.family f WHERE f.code IN :familyCodes")
  List<WideBodyAircraftEntity> findAllByFamilyCodes(@Param("familyCodes") List<String> familyCodes);

  @Query("SELECT w FROM WideBodyAircraftEntity w JOIN FETCH w.family")
  List<WideBodyAircraftEntity> findAllWithFamily();
}
