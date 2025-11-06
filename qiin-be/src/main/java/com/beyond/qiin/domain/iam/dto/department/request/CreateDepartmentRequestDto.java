package com.beyond.qiin.domain.iam.dto.department.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateDepartmentRequestDto {

    private String dptName;
}
