package project.vilsoncake.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "aircraft_families")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class AircraftFamilyEntity {

  @Id
  @Column(name = "code", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String code;

  @Column(name = "manufacturer", nullable = false)
  private String manufacturer;

  @OneToMany(mappedBy = "family")
  private List<WideBodyAircraftEntity> wideBodyAircraft;
}
