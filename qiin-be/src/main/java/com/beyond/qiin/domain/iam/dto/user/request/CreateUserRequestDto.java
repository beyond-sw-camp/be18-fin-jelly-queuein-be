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

    private String password;

    public static User toEntity(final CreateUserRequestDto dto) {
        return User.builder()
                .dptId(dto.getDptId())
                .userNo(dto.getUserNo())
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }
}
