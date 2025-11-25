package com.beyond.qiin.infra.redis.iam.role;

import com.beyond.qiin.domain.iam.entity.Role;
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
        log.info("[Redis SAVE] roleId={} roleName={}", role.getId(), role.getRoleName());

        RoleReadModel model = RoleReadModel.builder()
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .build();

        repository.save(model);
    }

    public void delete(final Long roleId) {
        log.info("[Redis DELETE] roleId={}", roleId);
        repository.deleteById(roleId);
    }
}
