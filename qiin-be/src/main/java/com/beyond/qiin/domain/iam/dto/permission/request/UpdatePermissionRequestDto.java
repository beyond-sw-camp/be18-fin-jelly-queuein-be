package com.beyond.qiin.domain.iam.dto.permission.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePermissionRequestDto {

    @NotBlank
    private String permissionName;

    private String permissionDescription;
}
