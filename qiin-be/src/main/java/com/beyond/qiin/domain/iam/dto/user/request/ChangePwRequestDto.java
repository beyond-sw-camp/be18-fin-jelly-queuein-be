package com.beyond.qiin.domain.iam.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePwRequestDto {

    @NotBlank
    private String currentPassword; // 기존 비밀번호

    @NotBlank
    private String newPassword; // 새 비밀번호
}
