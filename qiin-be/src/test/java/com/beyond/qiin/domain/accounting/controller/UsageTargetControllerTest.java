package com.beyond.qiin.domain.accounting.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.accounting.dto.usage_target.request.UsageTargetCreateRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetStatusResponseDto;
import com.beyond.qiin.domain.accounting.service.command.UsageTargetCommandService;
import com.beyond.qiin.domain.accounting.service.query.UsageTargetQueryService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UsageTargetController 단위 테스트")
class UsageTargetControllerTest {

    private UsageTargetController controller;
    private UsageTargetQueryService usageTargetQueryService;
    private UsageTargetCommandService usageTargetCommandService;

    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
        usageTargetQueryService = mock(UsageTargetQueryService.class);
        usageTargetCommandService = mock(UsageTargetCommandService.class);

        // Controller 생성자에 Mock 주입
        controller = new UsageTargetController(usageTargetQueryService, usageTargetCommandService);
    }

    // -----------------------------------------------
    // 1. 올해 목표 존재 여부 조회 (GET /current)
    // -----------------------------------------------

    @Test
    @DisplayName("getCurrentYearStatus - 목표 존재 시 성공")
    void getCurrentYearStatus_exists_success() {
        // Given
        int year = 2025;
        BigDecimal rate = new BigDecimal("0.75");
        UsageTargetStatusResponseDto mockResponse = UsageTargetStatusResponseDto.builder()
                .exists(true)
                .year(year)
                .targetRate(rate)
                .build();

        when(usageTargetQueryService.getCurrentYearStatus()).thenReturn(mockResponse);

        // When
        UsageTargetStatusResponseDto result = controller.getCurrentYearStatus();

        // Then
        assertThat(result.isExists()).isTrue();
        assertThat(result.getYear()).isEqualTo(year);
        assertThat(result.getTargetRate()).isEqualByComparingTo(rate);

        // Verify
        verify(usageTargetQueryService).getCurrentYearStatus();
    }

    @Test
    @DisplayName("getCurrentYearStatus - 목표 미존재 시 성공")
    void getCurrentYearStatus_notExists_success() {
        // Given
        int year = 2025;
        UsageTargetStatusResponseDto mockResponse = UsageTargetStatusResponseDto.builder()
                .exists(false)
                .year(year)
                .targetRate(null)
                .build();

        when(usageTargetQueryService.getCurrentYearStatus()).thenReturn(mockResponse);

        // When
        UsageTargetStatusResponseDto result = controller.getCurrentYearStatus();

        // Then
        assertThat(result.isExists()).isFalse();
        assertThat(result.getYear()).isEqualTo(year);
        assertThat(result.getTargetRate()).isNull();

        // Verify
        verify(usageTargetQueryService).getCurrentYearStatus();
    }

    // -----------------------------------------------
    // 2. 목표 등록 (POST /)
    // -----------------------------------------------

    @Test
    @DisplayName("create - 목표 등록 성공")
    void create_success() {
        // Given
        UsageTargetCreateRequestDto request = new UsageTargetCreateRequestDto();
        // private 필드에 값 설정 (Controller에서는 @RequestBody를 사용하므로 객체 생성만으로 충분하지만, 테스트를 위해 Reflection 사용)
        ReflectionTestUtils.setField(request, "targetRate", 0.85);

        UsageTargetResponseDto mockResponse = UsageTargetResponseDto.builder()
                .id(1L)
                .year(2025)
                .targetRate(new BigDecimal("0.85"))
                .createdBy(100L)
                .build();

        when(usageTargetCommandService.createTarget(any(UsageTargetCreateRequestDto.class)))
                .thenReturn(mockResponse);

        // When
        UsageTargetResponseDto result = controller.create(request);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getYear()).isEqualTo(2025);
        assertThat(result.getTargetRate()).isEqualByComparingTo("0.85");

        // Verify
        verify(usageTargetCommandService).createTarget(request);
    }

    // -----------------------------------------------
    // 3. 특정 연도 조회 (GET /{year})
    // -----------------------------------------------

    @Test
    @DisplayName("getByYear - 특정 연도 목표 조회 성공")
    void getByYear_success() {
        // Given
        int targetYear = 2023;
        BigDecimal rate = new BigDecimal("0.60");

        UsageTargetResponseDto mockResponse = UsageTargetResponseDto.builder()
                .id(2L)
                .year(targetYear)
                .targetRate(rate)
                .createdBy(50L)
                .build();

        when(usageTargetQueryService.getByYear(eq(targetYear))).thenReturn(mockResponse);

        // When
        UsageTargetResponseDto result = controller.getByYear(targetYear);

        // Then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getYear()).isEqualTo(targetYear);
        assertThat(result.getTargetRate()).isEqualByComparingTo(rate);

        // Verify
        verify(usageTargetQueryService).getByYear(targetYear);
    }
}
