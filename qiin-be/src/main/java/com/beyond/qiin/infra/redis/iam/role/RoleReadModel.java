package com.beyond.qiin.infra.redis.iam.role;

import java.io.Serializable;
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

    // TODO: ResponseDto 수정 시
    // private Integer userCount;
    // private List<String> permissions;
}
