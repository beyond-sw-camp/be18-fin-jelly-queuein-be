package com.beyond.qiin.domain.iam.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.dto.role.response.RoleListResponseDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.infra.redis.iam.role.RoleReadModel;
import com.beyond.qiin.infra.redis.iam.role.RoleRedisAdapter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("RoleQueryServiceImplTest 단위 테스트")
public class RoleQueryServiceImplTest {

    @Mock
    private RoleReader roleReader;

    @Mock
    private RoleRedisAdapter redisAdapter;

    @InjectMocks
    private RoleQueryServiceImpl roleQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Redis HIT 시 DB 조회 없이 Redis 값을 반환한다")
    void getRole_redis_hit() {
        Long roleId = 1L;

        RoleReadModel readModel = RoleReadModel.builder()
                .roleId(roleId)
                .roleName("ADMIN")
                .roleDescription("관리자")
                .build();

        when(redisAdapter.findById(roleId)).thenReturn(readModel);

        RoleResponseDto dto = roleQueryService.getRole(roleId);

        assertThat(dto.getRoleId()).isEqualTo(roleId);
        assertThat(dto.getRoleName()).isEqualTo("ADMIN");
        assertThat(dto.getRoleDescription()).isEqualTo("관리자");

        verify(roleReader, never()).findById(any());
    }

    @Test
    @DisplayName("Redis MISS 시 DB 조회 후 Redis 저장 및 DTO 반환한다")
    void getRole_redis_miss() {
        Long roleId = 10L;

        when(redisAdapter.findById(roleId)).thenReturn(null);

        Role role = Role.builder().roleName("DEV").roleDescription("개발자 역할").build();
        ReflectionTestUtils.setField(role, "id", roleId);

        when(roleReader.findById(roleId)).thenReturn(role);

        RoleResponseDto dto = roleQueryService.getRole(roleId);

        assertThat(dto.getRoleId()).isEqualTo(roleId);
        assertThat(dto.getRoleName()).isEqualTo("DEV");

        verify(roleReader).findById(roleId);
        verify(redisAdapter).save(role);
    }

    @Test
    @DisplayName("역할 목록 조회 시 DB 목록을 반환하며 Redis save 를 호출한다")
    void getRoles_success() {
        Role r1 = Role.builder().roleName("DEV").roleDescription("개발").build();
        Role r2 = Role.builder().roleName("OPS").roleDescription("운영").build();
        ReflectionTestUtils.setField(r1, "id", 1L);
        ReflectionTestUtils.setField(r2, "id", 2L);

        when(roleReader.findAll()).thenReturn(List.of(r1, r2));

        RoleListResponseDto list = roleQueryService.getRoles();

        assertThat(list.getRoles()).hasSize(2);
        assertThat(list.getRoles().get(0).getRoleName()).isEqualTo("DEV");
        assertThat(list.getRoles().get(1).getRoleName()).isEqualTo("OPS");

        verify(redisAdapter).save(r1);
        verify(redisAdapter).save(r2);
    }
}
