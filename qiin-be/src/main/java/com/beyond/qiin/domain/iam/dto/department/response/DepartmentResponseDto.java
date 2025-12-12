package com.beyond.qiin.domain.iam.dto.department.response;

import com.beyond.qiin.domain.iam.entity.Department;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentResponseDto {

    private final Long dptId;
    private final String dptName;
    private final Long userCount;

    public static DepartmentResponseDto of(final Department department, final Long userCount) {
        return DepartmentResponseDto.builder()
                .dptId(department.getId())
                .dptName(department.getDptName())
                .userCount(userCount)
                .build();
    }
}
