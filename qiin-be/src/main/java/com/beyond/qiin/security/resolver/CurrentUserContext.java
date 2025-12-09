package com.beyond.qiin.security.resolver;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserContext {

    private final Long userId;
    private final String email;
    private final String roleName;

    private final Map<String, List<String>> permissionGroups;

    private final String ipAddress;
    private final String userAgent;

    public static CurrentUserContext of(
            final Long userId,
            final String email,
            final String roleName,
            final Map<String, List<String>> permissionGroups,
            final String ipAddress,
            final String userAgent) {
        return CurrentUserContext.builder()
                .userId(userId)
                .email(email)
                .roleName(roleName)
                .permissionGroups(permissionGroups)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }
}
