package com.beyond.qiin.domain.iam.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeTempPwRequestDto {

    @NotBlank
    private String newPassword;
}
