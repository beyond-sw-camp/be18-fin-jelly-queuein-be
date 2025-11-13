package com.beyond.qiin.domain.auth.dto.request;

import lombok.Getter;

@Getter
public class SignupRequestDto {
    private Long dptId;
    private String userNo;
    private String userName;
    private String email;
}
