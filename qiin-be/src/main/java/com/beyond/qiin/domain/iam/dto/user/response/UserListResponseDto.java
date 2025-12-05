package com.beyond.qiin.domain.iam.dto.user.response;

import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserListResponseDto;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserListResponseDto {
    private final Long userId;
    private final String userName;
    private final String email;
    private final Long dptId;
    private final String roleName;
    private final String phone;
    private final Instant createdAt;
    private final Instant lastLoginAt;

    public static UserListResponseDto fromRaw(final RawUserListResponseDto raw) {
        return UserListResponseDto.builder()
                .userId(raw.getUserId())
                .userName(raw.getUserName())
                .email(raw.getEmail())
                .dptId(raw.getDptId())
                .roleName(raw.getRoleName())
                .phone(raw.getPhone())
                .createdAt(raw.getCreatedAt())
                .lastLoginAt(raw.getLastLoginAt())
                .build();
    }
}
