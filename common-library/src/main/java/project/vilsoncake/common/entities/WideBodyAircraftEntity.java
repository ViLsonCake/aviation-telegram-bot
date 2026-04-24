package project.vilsoncake.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.vilsoncake.common.entities.enums.AircraftPriority;

@Entity
@Table(name = "wide_body_aircraft")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class WideBodyAircraftEntity {

  @Id
  @Column(name = "code", nullable = false, updatable = false)
  @Setter(lombok.AccessLevel.NONE)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "family", nullable = false)
  private AircraftFamilyEntity family;

  @Column(name = "priority", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private AircraftPriority priority;
}
