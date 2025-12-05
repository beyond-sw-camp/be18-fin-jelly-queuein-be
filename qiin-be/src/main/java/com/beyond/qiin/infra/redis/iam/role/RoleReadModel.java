package com.beyond.qiin.infra.redis.iam.role;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash("role:read") // Redis key prefix: role:read:{id}
public class RoleReadModel implements Serializable {

    @Id
    private final Long roleId;

    private final String roleName;
    private final String roleDescription;

    private Integer userCount;
    private List<PermissionItem> permissions;

    @Getter
    @Builder
    public static class PermissionItem implements Serializable {
        private final Long permissionId;
        private final String permissionName;
        private final String permissionDescription;
    }
}
