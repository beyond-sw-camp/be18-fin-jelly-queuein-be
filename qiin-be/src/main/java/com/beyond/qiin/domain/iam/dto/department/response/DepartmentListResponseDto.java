package com.beyond.qiin.domain.iam.dto.department.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentListResponseDto {

    private final List<DepartmentResponseDto> departments;

    public static DepartmentListResponseDto from(final List<DepartmentResponseDto> dptList) {
        return DepartmentListResponseDto.builder().departments(dptList).build();
    }
}
