package com.beyond.qiin.domain.iam.dto.role.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateRoleRequestDto {

    @NotBlank(message = "역할명은 필수입니다.")
    @Size(max = 50)
    private String roleName;

    // 역할 설명
    @NotBlank(message = "역할 설명은 필수입니다.")
    private String roleDescription;
}
