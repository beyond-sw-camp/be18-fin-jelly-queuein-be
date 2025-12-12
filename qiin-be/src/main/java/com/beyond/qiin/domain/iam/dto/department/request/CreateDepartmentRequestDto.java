package com.beyond.qiin.domain.iam.dto.department.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateDepartmentRequestDto {

    @NotBlank
    @Size(max = 50)
    private String dptName;
}
