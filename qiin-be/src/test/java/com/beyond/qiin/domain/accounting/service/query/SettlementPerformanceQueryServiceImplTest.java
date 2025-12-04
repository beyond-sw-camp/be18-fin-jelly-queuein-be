package com.beyond.qiin.domain.accounting.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementPerformanceQueryAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("SettlementPerformanceQueryServiceImpl 단위 테스트")
class SettlementPerformanceQueryServiceImplTest {

    private SettlementPerformanceQueryAdapter queryAdapter;
    private SettlementPerformanceQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        queryAdapter = mock(SettlementPerformanceQueryAdapter.class);
        service = new SettlementPerformanceQueryServiceImpl(queryAdapter);

        // **NPE 방지 1순위 Mocking:** 어떤 호출에도 Null이 아닌 기본 Raw Data를 반환하도록 설정
        when(queryAdapter.getMonthlyPerformance(anyInt(), anyInt(), anyLong(), anyString()))
                .thenReturn(SettlementPerformanceRawDto.builder()
                        .assetId(0L)
                        .assetName("Default")
                        .baseYearData(Collections.emptyMap())
                        .compareYearData(Collections.emptyMap())
                        .build());
        when(queryAdapter.getTotalSavingAllTime()).thenReturn(BigDecimal.ZERO);
    }

    /**
     * Helper: Raw 데이터 Mock 생성
     */
    private SettlementPerformanceRawDto createMockRawData() {
        Map<Integer, BigDecimal> baseData = Map.of(1, new BigDecimal("100.00"), 2, new BigDecimal("-50.00"));
        Map<Integer, BigDecimal> compareData = Map.of(1, new BigDecimal("200.00"), 3, new BigDecimal("150.00"));

        return SettlementPerformanceRawDto.builder()
                .assetId(5L)
                .assetName("Server-X")
                .baseYearData(baseData)
                .compareYearData(compareData)
                .build();
    }

    @Test
    @DisplayName("getPerformance - 데이터 조회, 계산 및 응답 DTO 구성 성공 (Default Year)")
    void getPerformance_success() {
        // Given
        int currentYear = LocalDate.now().getYear();
        int baseYear = currentYear - 1;
        int compareYear = currentYear;

        // AssetId에 구체적인 값을 설정하여 Null이 아님을 보장
        ReportingComparisonRequestDto req = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(req, "assetId", 5L);
        ReflectionTestUtils.setField(req, "assetName", "Server-X"); // RawData와 일치하게 설정

        SettlementPerformanceRawDto mockRawData = createMockRawData();

        // 구체적인 Matcher를 사용하여 setUp의 Mocking을 확실히 재정의
        when(queryAdapter.getMonthlyPerformance(eq(baseYear), eq(compareYear), eq(5L), eq("Server-X")))
                .thenReturn(mockRawData);

        BigDecimal totalAccumulatedSaving = new BigDecimal("9999.99");
        when(queryAdapter.getTotalSavingAllTime()).thenReturn(totalAccumulatedSaving);

        // When
        SettlementPerformanceResponseDto result = service.getPerformance(req);

        // Then
        // Base Year Total: 100.00 + (-50.00) = 50.00
        assertThat(result.getSummary().getBaseYearTotalSaving()).isEqualByComparingTo("50.00");
        assertThat(result.getSummary().getCompareYearCurrentSaving()).isEqualByComparingTo("350.00");

        // Verify
        verify(queryAdapter).getMonthlyPerformance(eq(baseYear), eq(compareYear), eq(5L), eq("Server-X"));
        verify(queryAdapter).getTotalSavingAllTime();
    }

    @Test
    @DisplayName("getPerformance - 요청에 연도가 포함될 경우 해당 연도 사용")
    void getPerformance_withCustomYears_usesCustomYears() {
        // Given
        int customBase = 2020;
        int customCompare = 2021;

        ReportingComparisonRequestDto req = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(req, "baseYear", customBase);
        ReflectionTestUtils.setField(req, "compareYear", customCompare);

        // AssetId, AssetName을 구체적인 값으로 설정
        ReflectionTestUtils.setField(req, "assetId", 10L);
        ReflectionTestUtils.setField(req, "assetName", "CustomAsset");

        SettlementPerformanceRawDto mockRawData = SettlementPerformanceRawDto.builder()
                .assetId(10L)
                .assetName("CustomAsset")
                .baseYearData(Collections.emptyMap())
                .compareYearData(Collections.emptyMap())
                .build();

        // Mocking 재정의 (구체적인 인자 사용)
        when(queryAdapter.getMonthlyPerformance(eq(customBase), eq(customCompare), eq(10L), eq("CustomAsset")))
                .thenReturn(mockRawData);
        when(queryAdapter.getTotalSavingAllTime()).thenReturn(new BigDecimal("500.00"));

        // When
        SettlementPerformanceResponseDto result = service.getPerformance(req);

        // Then
        assertThat(result.getYearRange().getBaseYear()).isEqualTo(customBase);
        assertThat(result.getYearRange().getCompareYear()).isEqualTo(customCompare);
        assertThat(result.getAsset().getAssetId()).isEqualTo(10L);

        // Verify
        verify(queryAdapter).getMonthlyPerformance(eq(customBase), eq(customCompare), eq(10L), eq("CustomAsset"));
    }
}
