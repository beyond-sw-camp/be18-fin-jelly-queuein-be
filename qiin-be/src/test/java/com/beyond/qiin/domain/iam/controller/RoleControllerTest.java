package com.beyond.qiin.domain.iam.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.request.UpdateRoleRequestDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleListResponseDto;
import com.beyond.qiin.domain.iam.dto.role.response.RoleResponseDto;
import com.beyond.qiin.domain.iam.dto.role_permission.request.AddRolePermissionsRequestDto;
import com.beyond.qiin.domain.iam.dto.role_permission.request.ReplaceRolePermissionsRequestDto;
import com.beyond.qiin.domain.iam.dto.role_permission.response.RolePermissionListResponseDto;
import com.beyond.qiin.domain.iam.service.command.RoleCommandService;
import com.beyond.qiin.domain.iam.service.command.RolePermissionCommandService;
import com.beyond.qiin.domain.iam.service.query.RoleQueryService;
import java.lang.reflect.Constructor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("RoleControllerTest 단위 테스트")
class RoleControllerTest {

    private RoleController controller;
    private RoleCommandService roleCommandService;
    private RolePermissionCommandService rolePermissionCommandService;
    private RoleQueryService roleQueryService;

    @BeforeEach
    void setUp() {
        roleCommandService = mock(RoleCommandService.class);
        rolePermissionCommandService = mock(RolePermissionCommandService.class);
        roleQueryService = mock(RoleQueryService.class);

        controller = new RoleController(roleCommandService, rolePermissionCommandService, roleQueryService);
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            Constructor<T> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("createRole - 성공")
    void createRole_success() {
        CreateRoleRequestDto req = new CreateRoleRequestDto();
        ReflectionTestUtils.setField(req, "roleName", "ADMIN");
        ReflectionTestUtils.setField(req, "roleDescription", "관리자");

        RoleResponseDto response = RoleResponseDto.builder()
                .roleId(1L)
                .roleName("ADMIN")
                .roleDescription("관리자")
                .permissions(List.of())
                .build();

        when(roleCommandService.createRole(req)).thenReturn(response);

        ResponseEntity<RoleResponseDto> result = controller.createRole(req);

        assertThat(result.getBody().getRoleId()).isEqualTo(1L);
        verify(roleCommandService).createRole(req);
    }

    @Test
    @DisplayName("updateRole - 성공")
    void updateRole_success() {
        UpdateRoleRequestDto req = new UpdateRoleRequestDto();
        ReflectionTestUtils.setField(req, "roleName", "MOD");
        ReflectionTestUtils.setField(req, "roleDescription", "수정됨");

        RoleResponseDto response = RoleResponseDto.builder()
                .roleId(2L)
                .roleName("MOD")
                .roleDescription("수정됨")
                .permissions(List.of())
                .build();

        when(roleCommandService.updateRole(2L, req)).thenReturn(response);

        ResponseEntity<RoleResponseDto> result = controller.updateRole(2L, req);

        assertThat(result.getBody().getRoleName()).isEqualTo("MOD");
        verify(roleCommandService).updateRole(2L, req);
    }

    @Test
    @DisplayName("deleteRole - 성공")
    void deleteRole_success() {
        ResponseEntity<Void> res = controller.deleteRole(10L, 99L);

        verify(roleCommandService).deleteRole(10L, 99L);
        assertThat(res.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    @DisplayName("addPermissions - 성공")
    void addPermissions_success() {
        AddRolePermissionsRequestDto dto = createInstance(AddRolePermissionsRequestDto.class);

        ReflectionTestUtils.setField(dto, "permissionIds", List.of(1L, 2L));

        ResponseEntity<Void> result = controller.addPermissions(3L, dto);

        verify(rolePermissionCommandService).addPermissions(3L, List.of(1L, 2L));
        assertThat(result.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("replacePermissions - 성공")
    void replacePermissions_success() {
        ReplaceRolePermissionsRequestDto dto = createInstance(ReplaceRolePermissionsRequestDto.class);

        ReflectionTestUtils.setField(dto, "permissionIds", List.of(5L, 6L));

        RolePermissionListResponseDto response = RolePermissionListResponseDto.builder()
                .roleId(3L)
                .permissions(List.of())
                .build();

        when(rolePermissionCommandService.replacePermissions(3L, List.of(5L, 6L), 88L))
                .thenReturn(response);

        ResponseEntity<RolePermissionListResponseDto> res = controller.replacePermissions(3L, dto, 88L);

        assertThat(res.getBody().getRoleId()).isEqualTo(3L);
        verify(rolePermissionCommandService).replacePermissions(3L, List.of(5L, 6L), 88L);
    }

    @Test
    @DisplayName("deletePermission - 성공")
    void deletePermission_success() {
        ResponseEntity<Void> res = controller.deletePermission(10L, 5L, 99L);

        verify(rolePermissionCommandService).removePermission(10L, 5L, 99L);
        assertThat(res.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("getRole - 성공")
    void getRole_success() {
        RoleResponseDto dto = RoleResponseDto.builder()
                .roleId(7L)
                .roleName("USER")
                .roleDescription("사용자")
                .permissions(List.of())
                .build();

        when(roleQueryService.getRole(7L)).thenReturn(dto);

        ResponseEntity<RoleResponseDto> res = controller.getRole(7L);

        assertThat(res.getBody().getRoleId()).isEqualTo(7L);
        verify(roleQueryService).getRole(7L);
    }

    @Test
    @DisplayName("getRoleList - 성공")
    void getRoleList_success() {
        RoleListResponseDto dto = RoleListResponseDto.builder().roles(List.of()).build();

        when(roleQueryService.getRoles()).thenReturn(dto);

        ResponseEntity<RoleListResponseDto> res = controller.getRoleList();

        assertThat(res.getBody().getRoles()).isEmpty();
        verify(roleQueryService).getRoles();
    }
}
