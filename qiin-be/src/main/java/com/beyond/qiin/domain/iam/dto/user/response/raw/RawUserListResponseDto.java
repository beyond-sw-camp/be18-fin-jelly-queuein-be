package com.beyond.qiin.domain.iam.dto.user.response.raw;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawUserListResponseDto {

    private final Long userId;
    private final String userName;
    private final String email;
    private final Long dptId;
    private final String roleName;
    private final Instant createdAt;
    private final String phone;
    private final Instant lastLoginAt;
}
