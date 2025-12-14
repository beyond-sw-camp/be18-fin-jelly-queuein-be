package com.beyond.qiin.domain.iam.dto.user.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class UpdateMyInfoRequestDto {
    private String userName;

    @Email
    private String email;

    private String phone;

    private String birth;

    private String profileImageKey;

    private String profileImageUrl;
}
