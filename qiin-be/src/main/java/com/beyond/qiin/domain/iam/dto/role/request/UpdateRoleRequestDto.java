package com.beyond.qiin.domain.iam.dto.role.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateRoleRequestDto {

    @NotBlank(message = "역할명은 필수 입력 값입니다.")
    @Size(max = 50, message = "역할명은 최대 50자까지 가능합니다.")
    private String roleName;

    @Size(max = 255, message = "역할 설명은 최대 255자까지 가능합니다.")
    private String roleDescription;
}
