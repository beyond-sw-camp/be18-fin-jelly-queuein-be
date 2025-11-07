package com.beyond.qiin.domain.iam.dto.department.response;


import com.beyond.qiin.domain.iam.entity.Department;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentDetailResponseDto {

  private Long departmentId;
  private String departmentName;

  public static DepartmentDetailResponseDto fromEntity(final Department department) {
    return DepartmentDetailResponseDto.builder()
        .departmentId(department.getId())
        .departmentName(department.getDptName())
        .build();
  }


}
