package com.beyond.qiin.domain.iam.dto.user_role.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateUserRoleRequestDto {
    @NotNull
    private Long roleId;
}
