package com.beyond.qiin.domain.accounting.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementPerformanceQueryAdapter;
import com.beyond.qiin.infra.redis.accounting.settlement.SettlementPerformanceMonthRedisAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("SettlementPerformanceQueryServiceImpl 단위 테스트")
class SettlementPerformanceQueryServiceImplTest {

    private SettlementPerformanceQueryAdapter queryAdapter;
    private SettlementPerformanceMonthRedisAdapter redisAdapter;
    private SettlementPerformanceQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        queryAdapter = mock(SettlementPerformanceQueryAdapter.class);
        redisAdapter = mock(SettlementPerformanceMonthRedisAdapter.class);
        service = new SettlementPerformanceQueryServiceImpl(queryAdapter, redisAdapter);

        // Redis 기본 동작
        when(redisAdapter.get(anyString())).thenReturn(null);
        doNothing().when(redisAdapter).save(anyString(), any(BigDecimal.class));

        // DB 조회 Mock
        when(queryAdapter.getMonthlyPerformance(anyInt(), anyInt(), anyLong(), anyString()))
                .thenReturn(createMockRawData());

        when(queryAdapter.getTotalSavingAllTime()).thenReturn(BigDecimal.ZERO);

        // assetName → assetId
        when(queryAdapter.getAssetIdByName(eq("Server-X"))).thenReturn(10L);
        when(queryAdapter.getAssetIdByName(eq("CustomAsset"))).thenReturn(10L);
    }

    /**
     * Helper: Raw 데이터 Mock 생성
     */
    private SettlementPerformanceRawDto createMockRawData() {
        Map<Integer, BigDecimal> baseData = Map.of(1, new BigDecimal("100.00"), 2, new BigDecimal("-50.00"));
        Map<Integer, BigDecimal> compareData = Map.of(1, new BigDecimal("200.00"), 3, new BigDecimal("150.00"));

        return SettlementPerformanceRawDto.builder()
                .assetId(10L)
                .assetName("Server-X")
                .baseYearData(baseData)
                .compareYearData(compareData)
                .build();
    }

    @Test
    @DisplayName("getPerformance - 기본 연도 기준 조회 성공")
    void getPerformance_success() {
        // Given
        int currentYear = LocalDate.now().getYear();
        int baseYear = currentYear - 1;

        ReportingComparisonRequestDto req = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(req, "assetName", "Server-X");

        // When
        SettlementPerformanceResponseDto result = service.getPerformance(req);

        // Then
        // baseYearTotal = 100 + (-50) = 50
        assertThat(result.getSummary().getBaseYearTotalSaving()).isEqualByComparingTo("50.00");

        // compareYearTotal = 200 + 150 = 350
        assertThat(result.getSummary().getCompareYearCurrentSaving()).isEqualByComparingTo("350.00");

        // Verify
        verify(queryAdapter).getMonthlyPerformance(eq(baseYear), eq(currentYear), eq(10L), eq("Server-X"));
        verify(queryAdapter).getTotalSavingAllTime();
        verify(redisAdapter, atLeastOnce()).get(anyString());
    }

    @Test
    @DisplayName("getPerformance - 요청에 연도 지정 시 해당 연도 사용")
    void getPerformance_withCustomYears_usesCustomYears() {
        // Given
        int customBase = 2020;
        int customCompare = 2021;

        ReportingComparisonRequestDto req = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(req, "baseYear", customBase);
        ReflectionTestUtils.setField(req, "compareYear", customCompare);
        ReflectionTestUtils.setField(req, "assetName", "CustomAsset");

        // When
        SettlementPerformanceResponseDto result = service.getPerformance(req);

        // Then
        assertThat(result.getYearRange().getBaseYear()).isEqualTo(customBase);
        assertThat(result.getYearRange().getCompareYear()).isEqualTo(customCompare);
        assertThat(result.getAsset().getAssetId()).isEqualTo(10L);

        // Verify
        verify(queryAdapter).getMonthlyPerformance(eq(customBase), eq(customCompare), eq(10L), eq("CustomAsset"));
        verify(queryAdapter).getAssetIdByName(eq("CustomAsset"));
        verify(redisAdapter, atLeastOnce()).get(anyString());
    }
}
