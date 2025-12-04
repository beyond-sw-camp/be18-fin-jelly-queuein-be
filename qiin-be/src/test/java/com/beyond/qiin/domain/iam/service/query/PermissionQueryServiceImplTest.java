package com.beyond.qiin.domain.iam.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.dto.permission.response.PermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;
import com.beyond.qiin.domain.iam.entity.Permission;
import com.beyond.qiin.domain.iam.exception.PermissionException;
import com.beyond.qiin.domain.iam.support.permission.PermissionReader;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
;

@DisplayName("PermissionQueryServiceImpl 단위 테스트")
class PermissionQueryServiceImplTest {

    private PermissionQueryServiceImpl service;
    private PermissionReader permissionReader;

    @BeforeEach
    void setUp() {
        permissionReader = mock(PermissionReader.class);
        service = new PermissionQueryServiceImpl(permissionReader);
    }

    @Test
    @DisplayName("getPermission - 단일 권한 조회 성공")
    void getPermission_success() {
        Permission permission = Permission.builder()
                .permissionName("P_READ")
                .permissionDescription("읽기")
                .build();

        ReflectionTestUtils.setField(permission, "id", 10L);

        when(permissionReader.findById(10L)).thenReturn(permission);

        PermissionResponseDto dto = service.getPermission(10L);

        verify(permissionReader).findById(10L);

        assertThat(dto.getPermissionId()).isEqualTo(10L);
        assertThat(dto.getPermissionName()).isEqualTo("P_READ");
        assertThat(dto.getPermissionDescription()).isEqualTo("읽기");
    }

    @Test
    @DisplayName("getPermission - 권한 없음 → PermissionNotFound 예외")
    void getPermission_not_found() {
        when(permissionReader.findById(999L)).thenThrow(PermissionException.permissionNotFound());

        assertThatThrownBy(() -> service.getPermission(999L)).isInstanceOf(PermissionException.class);

        verify(permissionReader).findById(999L);
    }

    @Test
    @DisplayName("getPermissions - 전체 목록 조회 성공")
    void getPermissions_success() {
        Permission p1 = Permission.builder()
                .permissionName("P_READ")
                .permissionDescription("읽기")
                .build();
        ReflectionTestUtils.setField(p1, "id", 1L);

        Permission p2 = Permission.builder()
                .permissionName("P_WRITE")
                .permissionDescription("쓰기")
                .build();
        ReflectionTestUtils.setField(p2, "id", 2L);

        when(permissionReader.findAll()).thenReturn(List.of(p1, p2));

        PermissionListResponseDto dto = service.getPermissions();

        verify(permissionReader).findAll();

        assertThat(dto.getPermissions()).hasSize(2);

        assertThat(dto.getPermissions().get(0).getPermissionId()).isEqualTo(1L);
        assertThat(dto.getPermissions().get(0).getPermissionName()).isEqualTo("P_READ");

        assertThat(dto.getPermissions().get(1).getPermissionId()).isEqualTo(2L);
        assertThat(dto.getPermissions().get(1).getPermissionName()).isEqualTo("P_WRITE");
    }

    @Test
    @DisplayName("getPermissions - 빈 목록도 정상 반환")
    void getPermissions_empty() {
        when(permissionReader.findAll()).thenReturn(List.of());

        PermissionListResponseDto dto = service.getPermissions();

        verify(permissionReader).findAll();

        assertThat(dto.getPermissions()).isEmpty();
    }
}
