package project.vilsoncake.common.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_aircraft_family_filters")
@IdClass(UserAircraftFamilyFilterId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class UserAircraftFamilyFilterEntity {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aircraft_family_code", referencedColumnName = "code")
  private AircraftFamilyEntity aircraftFamily;
}
