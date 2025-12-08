package com.beyond.qiin.infra.redis.iam.role;

import com.beyond.qiin.domain.iam.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleProjectionHandler {

    private final RoleRedisAdapter redisAdapter;

    public void onRoleCreated(final Role role) {
        redisAdapter.save(role);
    }

    public void onRoleUpdated(final Role role) {
        redisAdapter.save(role);
    }

    public void onRolePermissionsChanged(final Role role) {
        redisAdapter.save(role);
    }

    public void onUserRoleChanged(final Role role) {
        redisAdapter.save(role);
    }

    public void onRoleDeleted(final Role role) {
        redisAdapter.delete(role.getId());
    }
}
