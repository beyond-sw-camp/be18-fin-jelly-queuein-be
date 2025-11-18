package com.beyond.qiin.domain.iam.dto.user.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeTempPwRequestDto {

    private String newPassword;
}
