package com.beyond.qiin.domain.iam.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.dto.permission.request.CreatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.request.UpdatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionListResponseDto;
import com.beyond.qiin.domain.iam.dto.permission.response.PermissionResponseDto;
import com.beyond.qiin.domain.iam.service.command.PermissionCommandService;
import com.beyond.qiin.domain.iam.service.query.PermissionQueryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("PermissionControllerTest 단위 테스트")
class PermissionControllerTest {

    private PermissionController controller;
    private PermissionCommandService commandService;
    private PermissionQueryService queryService;

    @BeforeEach
    void setUp() {
        commandService = mock(PermissionCommandService.class);
        queryService = mock(PermissionQueryService.class);
        controller = new PermissionController(commandService, queryService);
    }

    // -----------------------------------------------
    // Command
    // -----------------------------------------------

    @Test
    @DisplayName("createPermission - 성공")
    void createPermission_success() {

        CreatePermissionRequestDto request = new CreatePermissionRequestDto();
        ReflectionTestUtils.setField(request, "permissionName", "P_READ");
        ReflectionTestUtils.setField(request, "permissionDescription", "읽기");

        PermissionResponseDto response = PermissionResponseDto.builder()
                .permissionId(1L)
                .permissionName("P_READ")
                .permissionDescription("읽기")
                .build();

        when(commandService.createPermission(request)).thenReturn(response);

        ResponseEntity<PermissionResponseDto> result = controller.createPermission(request);

        assertThat(result.getBody().getPermissionId()).isEqualTo(1L);
        assertThat(result.getBody().getPermissionName()).isEqualTo("P_READ");

        verify(commandService).createPermission(request);
    }

    @Test
    @DisplayName("updatePermission 단위 테스트")
    void updatePermission_success() {
        UpdatePermissionRequestDto req = new UpdatePermissionRequestDto();
        ReflectionTestUtils.setField(req, "permissionName", "P_WRITE");
        ReflectionTestUtils.setField(req, "permissionDescription", "쓰기권한");

        PermissionResponseDto response = PermissionResponseDto.builder()
                .permissionId(5L)
                .permissionName("P_WRITE")
                .permissionDescription("쓰기권한")
                .build();

        when(commandService.updatePermission(5L, req)).thenReturn(response);

        ResponseEntity<PermissionResponseDto> result = controller.updatePermission(5L, req);

        assertThat(result.getBody().getPermissionName()).isEqualTo("P_WRITE");
        verify(commandService).updatePermission(5L, req);
    }

    @Test
    @DisplayName("deletePermission 단위 테스트")
    void deletePermission_success() {

        ResponseEntity<Void> result = controller.deletePermission(7L, 99L);

        verify(commandService).deletePermission(7L, 99L);
        assertThat(result.getStatusCode().value()).isEqualTo(204);
    }

    // -----------------------------------------------
    // Query
    // -----------------------------------------------

    @Test
    @DisplayName("getPermissions 단위 테스트")
    void getPermissions_success() {

        PermissionResponseDto p1 = PermissionResponseDto.builder()
                .permissionId(1L)
                .permissionName("P1")
                .permissionDescription("D1")
                .build();

        PermissionResponseDto p2 = PermissionResponseDto.builder()
                .permissionId(2L)
                .permissionName("P2")
                .permissionDescription("D2")
                .build();

        PermissionListResponseDto dto =
                PermissionListResponseDto.builder().permissions(List.of(p1, p2)).build();

        when(queryService.getPermissions()).thenReturn(dto);

        ResponseEntity<PermissionListResponseDto> result = controller.getPermissions();

        assertThat(result.getBody().getPermissions().size()).isEqualTo(2);
        verify(queryService).getPermissions();
    }

    @Test
    @DisplayName("getPermission 단위 테스트")
    void getPermission_success() {
        PermissionResponseDto dto = PermissionResponseDto.builder()
                .permissionId(2L)
                .permissionName("P_VIEW")
                .permissionDescription("조회")
                .build();

        when(queryService.getPermission(2L)).thenReturn(dto);

        ResponseEntity<PermissionResponseDto> result = controller.getPermission(2L);

        assertThat(result.getBody().getPermissionId()).isEqualTo(2L);
        verify(queryService).getPermission(2L);
    }
}
