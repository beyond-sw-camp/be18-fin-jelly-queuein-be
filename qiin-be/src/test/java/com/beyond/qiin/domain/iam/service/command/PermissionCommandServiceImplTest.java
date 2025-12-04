package com.beyond.qiin.domain.iam.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.dto.permission.request.CreatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.request.UpdatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;
import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.exception.PermissionException;
import com.beyond.qiin.domain.iam.support.permission.PermissionReader;
import com.beyond.qiin.domain.iam.support.permission.PermissionWriter;
import com.beyond.qiin.domain.iam.support.rolepermission.RolePermissionReader;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("PermissionCommandServiceImpl 단위 테스트")
class PermissionCommandServiceImplTest {

    private PermissionCommandServiceImpl service;

    private PermissionReader permissionReader;
    private PermissionWriter permissionWriter;
    private RolePermissionReader rolePermissionReader;

    @BeforeEach
    void setUp() {
        permissionReader = mock(PermissionReader.class);
        permissionWriter = mock(PermissionWriter.class);
        rolePermissionReader = mock(RolePermissionReader.class);

        service = new PermissionCommandServiceImpl(permissionReader, permissionWriter, rolePermissionReader);
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            java.lang.reflect.Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creating instance of " + clazz.getName(), e);
        }
    }

    @Test
    @DisplayName("createPermission - 생성 성공")
    void createPermission_success() {
        CreatePermissionRequestDto request = createInstance(CreatePermissionRequestDto.class);
        ReflectionTestUtils.setField(request, "permissionName", "P_READ");
        ReflectionTestUtils.setField(request, "permissionDescription", "읽기");

        doNothing().when(permissionReader).validateDuplication("P_READ");

        Permission created = Permission.builder()
                .permissionName("P_READ")
                .permissionDescription("읽기")
                .build();
        ReflectionTestUtils.setField(created, "id", 1L);

        when(permissionWriter.save(any(Permission.class))).thenReturn(created);

        PermissionResponseDto dto = service.createPermission(request);

        verify(permissionReader).validateDuplication("P_READ");
        verify(permissionWriter).save(any(Permission.class));

        assertThat(dto.getPermissionId()).isEqualTo(1L);
        assertThat(dto.getPermissionName()).isEqualTo("P_READ");
    }

    @Test
    @DisplayName("createPermission - 중복 권한명이면 예외 발생")
    void createPermission_duplicate_fail() {
        CreatePermissionRequestDto request = createInstance(CreatePermissionRequestDto.class);
        ReflectionTestUtils.setField(request, "permissionName", "P_READ");
        ReflectionTestUtils.setField(request, "permissionDescription", "읽기");

        doThrow(PermissionException.permissionAlreadyExists())
                .when(permissionReader)
                .validateDuplication("P_READ");

        assertThatThrownBy(() -> service.createPermission(request)).isInstanceOf(PermissionException.class);

        verify(permissionWriter, never()).save(any());
    }

    @Test
    @DisplayName("updatePermission - 수정 성공")
    void updatePermission_success() {
        Permission existed = Permission.builder()
                .permissionName("OLD")
                .permissionDescription("OLD DESC")
                .build();
        ReflectionTestUtils.setField(existed, "id", 10L);

        UpdatePermissionRequestDto request = createInstance(UpdatePermissionRequestDto.class);
        ReflectionTestUtils.setField(request, "permissionName", "NEW");
        ReflectionTestUtils.setField(request, "permissionDescription", "새 설명");

        when(permissionReader.findById(10L)).thenReturn(existed);
        when(permissionWriter.save(existed)).thenReturn(existed);

        PermissionResponseDto dto = service.updatePermission(10L, request);

        verify(permissionWriter).save(existed);
        assertThat(dto.getPermissionName()).isEqualTo("NEW");
        assertThat(dto.getPermissionDescription()).isEqualTo("새 설명");
    }

    @Test
    @DisplayName("updatePermission - 대상 없으면 예외 발생")
    void updatePermission_not_found() {
        UpdatePermissionRequestDto request = createInstance(UpdatePermissionRequestDto.class);
        ReflectionTestUtils.setField(request, "permissionName", "NEW");
        ReflectionTestUtils.setField(request, "permissionDescription", "DESC");

        when(permissionReader.findById(999L)).thenThrow(PermissionException.permissionNotFound());

        assertThatThrownBy(() -> service.updatePermission(999L, request)).isInstanceOf(PermissionException.class);

        verify(permissionWriter, never()).save(any());
    }

    @Test
    @DisplayName("deletePermission - 삭제 성공")
    void deletePermission_success() {
        Long permissionId = 5L;
        Long deleterId = 99L;

        Permission permission = Permission.builder()
                .permissionName("P_DEL")
                .permissionDescription("삭제 가능")
                .build();
        ReflectionTestUtils.setField(permission, "id", permissionId);

        when(permissionReader.findById(permissionId)).thenReturn(permission);
        when(rolePermissionReader.existsByPermission(permission)).thenReturn(false);
        when(permissionWriter.save(any())).thenReturn(permission);

        service.deletePermission(permissionId, deleterId);

        verify(permissionWriter).save(any(Permission.class));
        assertThat(permission.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("deletePermission - 이미 삭제된 권한이면 PermissionAlreadyDeleted 예외 발생")
    void deletePermission_alreadyDeleted_fail() {
        Long permissionId = 5L;

        Permission permission = Permission.builder()
                .permissionName("P_DEL")
                .permissionDescription("삭제됨")
                .build();

        ReflectionTestUtils.setField(permission, "id", permissionId);
        ReflectionTestUtils.setField(permission, "deletedAt", Instant.now());

        when(permissionReader.findById(permissionId)).thenReturn(permission);

        assertThatThrownBy(() -> service.deletePermission(permissionId, 99L)).isInstanceOf(PermissionException.class);

        verify(rolePermissionReader, never()).existsByPermission(any());
        verify(permissionWriter, never()).save(any());
    }

    @Test
    @DisplayName("deletePermission - RolePermission에서 사용 중이면 PermissionInUse 예외 발생")
    void deletePermission_in_use_fail() {
        Long permissionId = 5L;

        Permission permission = Permission.builder()
                .permissionName("P_DEL")
                .permissionDescription("사용중")
                .build();
        ReflectionTestUtils.setField(permission, "id", permissionId);

        when(permissionReader.findById(permissionId)).thenReturn(permission);
        when(rolePermissionReader.existsByPermission(permission)).thenReturn(true);

        assertThatThrownBy(() -> service.deletePermission(permissionId, 99L)).isInstanceOf(PermissionException.class);

        verify(permissionWriter, never()).save(any());
    }
}
