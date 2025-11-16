package com.beyond.qiin.domain.iam.dto.user.request;

import com.beyond.qiin.domain.iam.entity.User;
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

    public static User toEntity(final CreateUserRequestDto request) {
        return User.builder()
                .dptId(request.getDptId())
                .userNo(request.getUserNo())
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}
