package com.beyond.qiin.infra.redis.iam.role;

import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.infra.redis.iam.role.RoleReadModel.PermissionItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleRedisAdapter {

    private final RoleRedisRepository roleRedisRepository;

    public RoleReadModel findById(final Long id) {
        RoleReadModel model = roleRedisRepository.findById(id).orElse(null);
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

        RoleReadModel roleReadModel = RoleReadModel.builder()
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .permissions(permissions)
                .userCount(userCount)
                .build();
        log.info("[Redis SAVE] roleId={} userCount={}", roleReadModel.getRoleId(), roleReadModel.getUserCount());
        roleRedisRepository.save(roleReadModel);
    }

    public void delete(final Long roleId) {
        log.info("[Redis DELETE] roleId={}", roleId);
        roleRedisRepository.deleteById(roleId);
    }
}
