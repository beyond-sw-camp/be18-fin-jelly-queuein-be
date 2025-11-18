package com.beyond.qiin.domain.iam.dto.user.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateUserRequestDto {

    private Long dptId;

    private String userNo;

    private String userName;

    private String email;
}
