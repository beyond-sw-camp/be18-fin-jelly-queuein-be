package com.beyond.qiin.infra.redis.iam.role;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.infra.redis.iam.role.RoleReadModel.PermissionItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleRedisAdapter {

    private final RoleRedisRepository repository;

    public RoleReadModel findById(final Long id) {
        RoleReadModel model = repository.findById(id).orElse(null);
        if (model == null) {
            log.info("[Redis MISS] roleId={}", id);
        } else {
            log.info("[Redis HIT] roleId={} -> {}", id, model.getRoleName());
        }
        return model;
    }

    public void save(final Role role) {

        List<PermissionItem> permissions = role.getRolePermissions().stream()
                .map(rp -> RoleReadModel.PermissionItem.builder()
                        .permissionId(rp.getPermission().getId())
                        .permissionName(rp.getPermission().getPermissionName())
                        .permissionDescription(rp.getPermission().getPermissionDescription())
                        .build())
                .toList();

        log.info("[Redis SAVE] roleId={} roleName={}", role.getId(), role.getRoleName());

        int userCount = role.getUserRoles().size();

        RoleReadModel model = RoleReadModel.builder()
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .permissions(permissions)
                .userCount(userCount)
                .build();
        log.info("[Redis SAVE] roleId={} userCount={}", model.getRoleId(), model.getUserCount());
        repository.save(model);
    }

    public void delete(final Long roleId) {
        log.info("[Redis DELETE] roleId={}", roleId);
        repository.deleteById(roleId);
    }
}
