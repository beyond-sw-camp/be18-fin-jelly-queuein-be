package com.beyond.qiin.domain.accounting.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryListSearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryQueryService;
import com.beyond.qiin.domain.accounting.service.query.UsageHistoryTrendQueryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UsageHistoryController 단위 테스트")
class UsageHistoryControllerTest {

    private UsageHistoryController controller;
    private UsageHistoryQueryService usageHistoryService;
    private UsageHistoryTrendQueryService usageHistoryTrendService;

    @BeforeEach
    void setUp() {
        // 실제 Controller의 생성자에 맞게 Mock 객체 초기화
        usageHistoryService = mock(UsageHistoryQueryService.class);
        usageHistoryTrendService = mock(UsageHistoryTrendQueryService.class);

        // 실제 Controller 생성자에 Mock 주입
        controller = new UsageHistoryController(usageHistoryService, usageHistoryTrendService);
    }

    // -----------------------------------------------
    // 1. 사용 내역 목록 조회 (GET /)
    // -----------------------------------------------

    @Test
    @DisplayName("listUsageHistory - 성공")
    void listUsageHistory_success() {
        // Given
        UsageHistoryListSearchRequestDto request = new UsageHistoryListSearchRequestDto();
        ReflectionTestUtils.setField(request, "startDate", LocalDate.of(2025, 1, 1));
        ReflectionTestUtils.setField(request, "keyword", "ServerA");

        Pageable pageable = PageRequest.of(0, 20); // @PageableDefault(size = 20)에 맞춤

        UsageHistoryListResponseDto mockDto = new UsageHistoryListResponseDto(
                100L,
                "AssetX",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                60,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                50,
                BigDecimal.valueOf(0.833));

        Page<UsageHistoryListResponseDto> mockPage = new PageImpl<>(List.of(mockDto), pageable, 1);
        PageResponseDto<UsageHistoryListResponseDto> mockResponse = PageResponseDto.from(mockPage);

        when(usageHistoryService.getUsageHistoryList(any(UsageHistoryListSearchRequestDto.class), any(Pageable.class)))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<PageResponseDto<UsageHistoryListResponseDto>> result =
                controller.listUsageHistory(request, pageable);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getContent().size()).isEqualTo(1);
        assertThat(result.getBody().getContent().get(0).getAssetName()).isEqualTo("AssetX");

        // Verify
        verify(usageHistoryService).getUsageHistoryList(request, pageable);
    }

    // -----------------------------------------------
    // 2. 사용 내역 상세 조회 (GET /{id})
    // -----------------------------------------------

    @Test
    @DisplayName("usageHistoryDetail - 성공")
    void usageHistoryDetail_success() {
        // Given
        Long usageHistoryId = 5L;
        UsageHistoryDetailResponseDto mockResponse = UsageHistoryDetailResponseDto.builder()
                .usageHistoryId(usageHistoryId)
                .assetName("GPU-Server-3")
                .reserverNames(List.of("UserA", "UserB"))
                .billAmount(new BigDecimal("12000.00"))
                .actualBillAmount(new BigDecimal("10000.00"))
                .build();

        when(usageHistoryService.getUsageHistoryDetail(usageHistoryId)).thenReturn(mockResponse);

        // When
        ResponseEntity<UsageHistoryDetailResponseDto> result = controller.usageHistoryDetail(usageHistoryId);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getUsageHistoryId()).isEqualTo(usageHistoryId);
        assertThat(result.getBody().getReserverNames()).containsExactly("UserA", "UserB");

        // Verify
        verify(usageHistoryService).getUsageHistoryDetail(usageHistoryId);
    }

    // -----------------------------------------------
    // 3. 사용 내역 추이 조회 (GET /trend)
    // -----------------------------------------------

    @Test
    @DisplayName("usageTrend - 성공")
    void usageTrend_success() {
        // Given
        ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(request, "baseYear", 2024);
        ReflectionTestUtils.setField(request, "compareYear", 2025);
        ReflectionTestUtils.setField(request, "assetName", "MainServer");

        UsageHistoryTrendResponseDto mockResponse = UsageHistoryTrendResponseDto.builder()
                .asset(UsageHistoryTrendResponseDto.AssetInfo.builder()
                        .assetId(1L)
                        .assetName("MainServer")
                        .build())
                .summary(UsageHistoryTrendResponseDto.UsageIncreaseSummary.builder()
                        .usageRateIncrease(5.5)
                        .build())
                .monthlyData(Collections.emptyList())
                .build();

        when(usageHistoryTrendService.getUsageHistoryTrend(any(ReportingComparisonRequestDto.class)))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<UsageHistoryTrendResponseDto> result = controller.usageTrend(request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getAsset().getAssetName()).isEqualTo("MainServer");
        assertThat(result.getBody().getSummary().getUsageRateIncrease()).isEqualTo(5.5);

        // Verify
        verify(usageHistoryTrendService).getUsageHistoryTrend(request);
    }
}
