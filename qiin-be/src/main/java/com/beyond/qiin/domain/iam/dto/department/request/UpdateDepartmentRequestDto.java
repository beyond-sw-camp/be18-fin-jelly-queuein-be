package com.beyond.qiin.domain.iam.dto.department.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateDepartmentRequestDto {

    private String dptName;
}
