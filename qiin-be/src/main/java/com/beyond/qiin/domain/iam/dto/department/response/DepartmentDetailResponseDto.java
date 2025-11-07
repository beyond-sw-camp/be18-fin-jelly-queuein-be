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

    private Long dptId;
    private String dptName;

    public static DepartmentDetailResponseDto fromEntity(final Department department) {
        return DepartmentDetailResponseDto.builder()
                .dptId(department.getId())
                .dptName(department.getDptName())
                .build();
    }
}
