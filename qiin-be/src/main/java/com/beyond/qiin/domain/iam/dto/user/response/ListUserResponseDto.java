package com.beyond.qiin.domain.iam.dto.user.response;

import com.beyond.qiin.domain.iam.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ListUserResponseDto {

    private Long userId;
    private String userName;
    private String email;
    private Boolean passwordExpired;

    public static ListUserResponseDto fromEntity(final User user) {
        return ListUserResponseDto.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .passwordExpired(user.getPasswordExpired())
                .build();
    }
}
