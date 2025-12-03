package com.beyond.qiin.domain.iam.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.request.UpdateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.exception.RoleException;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.role.RoleWriter;
import com.beyond.qiin.infra.redis.iam.role.RoleProjectionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("RoleCommandServiceImplTest 단위 테스트")
public class RoleCommandServiceImplTest {

    @Mock
    private RoleReader roleReader;

    @Mock
    private RoleWriter roleWriter;

    @Mock
    private RoleProjectionHandler projectionHandler;

    @InjectMocks
    private RoleCommandServiceImpl roleCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("역할 생성 성공 단위 테스트")
    void createRole_success() {
        // given
        CreateRoleRequestDto req = mock(CreateRoleRequestDto.class);
        when(req.getRoleName()).thenReturn("DEV");
        when(req.getRoleDescription()).thenReturn("개발자 역할");

        Role saved = Role.builder().roleName("DEV").roleDescription("개발자 역할").build();

        ReflectionTestUtils.setField(saved, "id", 100L);

        doNothing().when(roleReader).validateNameDuplication("DEV");
        when(roleWriter.save(any(Role.class))).thenReturn(saved);

        // when
        RoleResponseDto dto = roleCommandService.createRole(req);

        // then
        assertThat(dto.getRoleId()).isEqualTo(100L);
        assertThat(dto.getRoleName()).isEqualTo("DEV");

        verify(roleReader).validateNameDuplication("DEV");
        verify(roleWriter).save(any(Role.class));
        verify(projectionHandler).onRoleCreated(saved);
    }

    @Test
    @DisplayName("역할 수정 성공 단위 테스트")
    void updateRole_success() {
        // given
        Long roleId = 10L;
        UpdateRoleRequestDto req = mock(UpdateRoleRequestDto.class);

        when(req.getRoleName()).thenReturn("DEV"); // ← 시스템 역할 아님
        when(req.getRoleDescription()).thenReturn("개발자 역할");

        Role role = mock(Role.class);
        when(roleReader.findById(roleId)).thenReturn(role);
        when(role.getRoleName()).thenReturn("OLD_ROLE"); // 기존 역할명도 시스템 역할이면 안 됨

        Role updated = Role.builder().roleName("DEV").roleDescription("개발자 역할").build();
        ReflectionTestUtils.setField(updated, "id", roleId);

        when(roleWriter.save(role)).thenReturn(updated);

        // when
        RoleResponseDto dto = roleCommandService.updateRole(roleId, req);

        // then
        verify(role).update("DEV", "개발자 역할");
        verify(roleWriter).save(role);
        verify(projectionHandler).onRoleUpdated(updated);

        assertThat(dto.getRoleName()).isEqualTo("DEV");
        assertThat(dto.getRoleDescription()).isEqualTo("개발자 역할");
    }

    @Test
    @DisplayName("MASTER 역할 수정 시 예외 발생")
    void updateRole_fail_masterProtected() {
        // given
        Long roleId = 5L;
        UpdateRoleRequestDto req = mock(UpdateRoleRequestDto.class);

        Role role = mock(Role.class);
        when(roleReader.findById(roleId)).thenReturn(role);
        when(role.getRoleName()).thenReturn("MASTER");

        // when & then
        assertThatThrownBy(() -> roleCommandService.updateRole(roleId, req)).isInstanceOf(RoleException.class);

        verify(roleWriter, never()).save(any());
    }

    @Test
    @DisplayName("시스템 기본 역할 수정 시 예외 발생")
    void updateRole_fail_systemRoleProtected() {
        // given
        Long roleId = 3L;
        UpdateRoleRequestDto req = mock(UpdateRoleRequestDto.class);

        Role role = mock(Role.class);
        when(roleReader.findById(roleId)).thenReturn(role);
        when(role.getRoleName()).thenReturn("ADMIN"); // SYSTEM_ROLES 에 포함됨

        // when & then
        assertThatThrownBy(() -> roleCommandService.updateRole(roleId, req)).isInstanceOf(RoleException.class);
    }

    // ---------------------------------------------------
    // deleteRole()
    // ---------------------------------------------------
    @Test
    @DisplayName("역할 삭제 성공 단위 테스트")
    void deleteRole_success() {
        // given
        Long roleId = 7L;
        Long deleterId = 99L;

        Role role = mock(Role.class);
        when(roleReader.findById(roleId)).thenReturn(role);
        when(role.getRoleName()).thenReturn("DEV");

        // when
        roleCommandService.deleteRole(roleId, deleterId);

        // then
        verify(role).softDelete(deleterId);
        verify(projectionHandler).onRoleDeleted(role);
    }

    @Test
    @DisplayName("MASTER 역할 삭제 시 예외 발생")
    void deleteRole_fail_masterProtected() {
        // given
        Role role = mock(Role.class);
        when(roleReader.findById(1L)).thenReturn(role);
        when(role.getRoleName()).thenReturn("MASTER");

        // when & then
        assertThatThrownBy(() -> roleCommandService.deleteRole(1L, 20L)).isInstanceOf(RoleException.class);

        verify(projectionHandler, never()).onRoleDeleted(any());
    }

    @Test
    @DisplayName("시스템 기본 역할 삭제 시 예외 발생")
    void deleteRole_fail_systemRoleProtected() {
        // given
        Role role = mock(Role.class);
        when(roleReader.findById(5L)).thenReturn(role);
        when(role.getRoleName()).thenReturn("ADMIN"); // SYSTEM_ROLES 에 포함됨

        // when & then
        assertThatThrownBy(() -> roleCommandService.deleteRole(5L, 20L)).isInstanceOf(RoleException.class);

        verify(projectionHandler, never()).onRoleDeleted(any());
    }
}
