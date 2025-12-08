package com.beyond.qiin.security.resolver;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserContext {

    private final Long userId;
    private final String email;
    private final String roleName;
    private final List<String> permissions;

    private final String ipAddress;
    private final String userAgent;

    public static CurrentUserContext of(
            final Long userId,
            final String email,
            final String roleName,
            final List<String> permissions,
            final String ipAddress,
            final String userAgent) {
        return CurrentUserContext.builder()
                .userId(userId)
                .email(email)
                .roleName(roleName)
                .permissions(permissions)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }
}
