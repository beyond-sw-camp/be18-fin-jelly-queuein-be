package com.beyond.qiin.domain.iam.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class DepartmentClosureId {

  @Column(name = "ancestor_id")
  private Long ancestorId;

  @Column(name = "descendant_id")
  private Long descendantId;

}
