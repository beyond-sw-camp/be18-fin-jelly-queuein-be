package com.beyond.qiin.domain.iam.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionResponseDto;
import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.entity.Role;
import com.beyond.qiin.domain.iam.entity.RolePermission;
import com.beyond.qiin.domain.iam.exception.PermissionException;
import com.beyond.qiin.domain.iam.support.permission.PermissionReader;
import com.beyond.qiin.domain.iam.support.role.RoleReader;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionReader;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionWriter;
import com.beyond.qiin.infra.redis.iam.role.RoleProjectionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("RolePermissionCommandServiceImpl 단위 테스트")
class RolePermissionCommandServiceImplTest {

    private RolePermissionCommandServiceImpl service;

    private RoleReader roleReader;
    private PermissionReader permissionReader;

    private RolePermissionReader rolePermissionReader;
    private RolePermissionWriter rolePermissionWriter;

    private RoleProjectionHandler roleProjectionHandler;

    private Role role;
    private Permission p1;
    private Permission p2;

    @BeforeEach
    void setUp() {
        // --- mock 생성 ---
        roleReader = mock(RoleReader.class);
        permissionReader = mock(PermissionReader.class);
        rolePermissionReader = mock(RolePermissionReader.class);
        rolePermissionWriter = mock(RolePermissionWriter.class);
        roleProjectionHandler = mock(RoleProjectionHandler.class);

        // --- Service 생성 ---
        service = new RolePermissionCommandServiceImpl(
                roleReader, permissionReader, rolePermissionReader, rolePermissionWriter, roleProjectionHandler);

        // --- 실제 엔티티 생성 ---
        role = Role.builder().roleName("DEV").roleDescription("개발자").build();
        p1 = Permission.builder()
                .permissionName("perm.read")
                .permissionDescription("읽기")
                .build();
        p2 = Permission.builder()
                .permissionName("perm.write")
                .permissionDescription("쓰기")
                .build();

        // --- ID Reflection 설정 ---
        ReflectionTestUtils.setField(role, "id", 1L);
        ReflectionTestUtils.setField(p1, "id", 10L);
        ReflectionTestUtils.setField(p2, "id", 20L);
    }

    @Test
    @DisplayName("addPermission - 단일 권한 추가 성공")
    void addPermission_success() {
        when(roleReader.findById(1L)).thenReturn(role);
        when(permissionReader.findById(10L)).thenReturn(p1);
        when(rolePermissionReader.existsByRoleAndPermission(role, p1)).thenReturn(false);

        RolePermission entity = RolePermission.create(role, p1);
        when(rolePermissionWriter.save(any())).thenReturn(entity);

        RolePermissionResponseDto dto = service.addPermission(1L, 10L);

        verify(rolePermissionWriter).save(any(RolePermission.class));
        verify(roleProjectionHandler).onRolePermissionsChanged(role);
        assertThat(dto.getPermissionId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("addPermission - 이미 존재하면 PermissionException 발생")
    void addPermission_duplicate_fail() {
        when(roleReader.findById(1L)).thenReturn(role);
        when(permissionReader.findById(10L)).thenReturn(p1);
        when(rolePermissionReader.existsByRoleAndPermission(role, p1)).thenReturn(true);

        assertThatThrownBy(() -> service.addPermission(1L, 10L)).isInstanceOf(PermissionException.class);

        verify(roleProjectionHandler, never()).onRolePermissionsChanged(any());
    }

    @Test
    @DisplayName("addPermissions - 여러 권한 추가 성공")
    void addPermissions_success() {
        when(roleReader.findById(1L)).thenReturn(role);
        when(permissionReader.findById(10L)).thenReturn(p1);
        when(permissionReader.findById(20L)).thenReturn(p2);
        when(rolePermissionReader.existsByRoleAndPermission(any(), any())).thenReturn(false);

        List<RolePermission> savedList = List.of(RolePermission.create(role, p1), RolePermission.create(role, p2));

        when(rolePermissionWriter.saveAll(any())).thenReturn(savedList);

        RolePermissionListResponseDto dto = service.addPermissions(1L, List.of(10L, 20L));

        assertThat(dto.getPermissions()).hasSize(2);
        verify(rolePermissionWriter).saveAll(any());
    }

    @Test
    @DisplayName("replacePermissions - 기존 softDelete 후 새로운 매핑 추가")
    void replacePermissions_success() {

        RolePermission existed = RolePermission.create(role, p1);

        when(roleReader.findById(1L)).thenReturn(role);
        when(rolePermissionReader.findAllByRole(role)).thenReturn(List.of(existed));

        when(permissionReader.findById(20L)).thenReturn(p2);
        when(rolePermissionReader.existsByRoleAndPermission(role, p2)).thenReturn(false);

        List<RolePermission> newList = List.of(RolePermission.create(role, p2));
        when(rolePermissionWriter.saveAll(any())).thenReturn(newList);

        RolePermissionListResponseDto dto = service.replacePermissions(1L, List.of(20L), 99L);

        assertThat(dto.getPermissions()).hasSize(1);
        verify(rolePermissionWriter, times(2)).saveAll(any());
        verify(roleProjectionHandler).onRolePermissionsChanged(role);
    }

    @Test
    @DisplayName("removePermission - softDelete 후 남은 목록 반환")
    void removePermission_success() {

        RolePermission rp1 = RolePermission.create(role, p1);
        RolePermission rp2 = RolePermission.create(role, p2);

        when(roleReader.findById(1L)).thenReturn(role);
        when(rolePermissionReader.findAllByRole(role))
                .thenReturn(List.of(rp1, rp2)) // 첫 조회
                .thenReturn(List.of(rp2)); // softDelete 이후 조회

        RolePermissionListResponseDto dto = service.removePermission(1L, 10L, 77L);

        assertThat(dto.getPermissions()).hasSize(1);
        assertThat(dto.getPermissions().getFirst().getPermissionId()).isEqualTo(20L);
        verify(rolePermissionWriter).saveAll(any());
    }

    @Test
    @DisplayName("removePermission - 해당 권한 없으면 예외 발생")
    void removePermission_not_found() {
        when(roleReader.findById(1L)).thenReturn(role);
        when(rolePermissionReader.findAllByRole(role)).thenReturn(List.of());

        assertThatThrownBy(() -> service.removePermission(1L, 999L, 77L)).isInstanceOf(PermissionException.class);
    }
}
