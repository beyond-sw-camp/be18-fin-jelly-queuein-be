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
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.MonthlyUsageData;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto.UsageIncreaseSummary;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryTrendQueryAdapter;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UsageHistoryTrendQueryServiceImpl 단위 테스트")
class UsageHistoryTrendQueryServiceImplTest {

    private final int FIXED_YEAR = 2025;
    private final int FIXED_MONTH = 6;
    private UsageHistoryTrendQueryServiceImpl service;
    private UsageHistoryTrendQueryAdapter trendQueryAdapter;

    @BeforeEach
    void setUp() {
        trendQueryAdapter = mock(UsageHistoryTrendQueryAdapter.class);
        service = new UsageHistoryTrendQueryServiceImpl(trendQueryAdapter);
    }

    /**
     * Helper: Raw 데이터를 생성합니다.
     */
    private UsageHistoryTrendRawDto createMockRawData(int assetCount) {
        Map<Integer, UsageAggregate> baseData = Map.of(
                1, UsageAggregate.builder().reservedUsage(1000).actualUsage(800).build(),
                2, UsageAggregate.builder().reservedUsage(1000).actualUsage(500).build(),
                3, UsageAggregate.builder().reservedUsage(1000).actualUsage(600).build(),
                4, UsageAggregate.builder().reservedUsage(1000).actualUsage(900).build());

        Map<Integer, UsageAggregate> compareData = Map.of(
                1, UsageAggregate.builder().reservedUsage(1000).actualUsage(900).build(),
                2, UsageAggregate.builder().reservedUsage(1200).actualUsage(720).build(),
                4, UsageAggregate.builder().reservedUsage(800).actualUsage(640).build());

        return UsageHistoryTrendRawDto.builder()
                .assetId(10L)
                .assetName("Trend-Asset")
                .assetCount(assetCount)
                .baseYearData(baseData)
                .compareYearData(compareData)
                .build();
    }

    // -------------------------------------------------------------------------
    // 1. 월별 추이 조회 (Monthly List) 검증
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("buildMonthlyList - 1월부터 12월까지의 사용률을 정확히 계산하여 반환")
    void buildMonthlyList_generatesAllMonths() {
        // Given
        int baseYear = FIXED_YEAR - 1;
        int compareYear = FIXED_YEAR;

        ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(request, "assetId", 10L);
        ReflectionTestUtils.setField(request, "assetName", "Trend-Asset");

        UsageHistoryTrendRawDto mockRawData = createMockRawData(1);

        when(trendQueryAdapter.getTrendData(eq(baseYear), eq(compareYear), eq(10L), eq("Trend-Asset"), anyInt()))
                .thenReturn(mockRawData);

        final LocalDate fixedDate = LocalDate.of(FIXED_YEAR, FIXED_MONTH, 1);

        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now).thenReturn(fixedDate);

            // When
            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);

            // Then
            assertThat(result.getMonthlyData()).hasSize(12);
            MonthlyUsageData month1 = result.getMonthlyData().get(0);

            // 1월 검증
            assertThat(month1.getBaseYearUsageRate()).isEqualTo(80.0);
            assertThat(month1.getCompareYearUsageRate()).isEqualTo(90.0);

            verify(trendQueryAdapter).getTrendData(eq(baseYear), eq(compareYear), eq(10L), eq("Trend-Asset"), anyInt());
        }
    }

    // -------------------------------------------------------------------------
    // 2. Summary 계산 (calculateIncrease) 검증
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("calculateIncrease - 유효 월 기반 증가율 및 활용률 계산 검증")
    void calculateIncrease_calculatesSummaryCorrectly() {
        // Given
        int assetCount = 10;
        UsageHistoryTrendRawDto mockRawData = createMockRawData(assetCount);

        when(trendQueryAdapter.getTrendData(anyInt(), anyInt(), anyLong(), anyString(), anyInt()))
                .thenReturn(mockRawData);

        final LocalDate fixedDate = LocalDate.of(FIXED_YEAR, FIXED_MONTH, 1);

        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now).thenReturn(fixedDate);

            ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
            ReflectionTestUtils.setField(request, "assetId", 10L);
            ReflectionTestUtils.setField(request, "assetName", "Trend-Asset");

            // Expected Calculations (0.0% / 2.0% / 0.0%)
            double expectedActualUsageIncrease = 2.0;

            // When
            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);
            UsageIncreaseSummary summary = result.getSummary();

            // Then
            assertThat(summary.getUsageRateIncrease()).isEqualTo(0.0);
            assertThat(summary.getActualUsageIncrease()).isEqualTo(expectedActualUsageIncrease);
            assertThat(summary.getResourceUtilizationIncrease()).isEqualTo(0.0);

            verify(trendQueryAdapter).getTrendData(anyInt(), anyInt(), eq(10L), eq("Trend-Asset"), anyInt());
        }
    }

    @Test
    @DisplayName("calculateIncrease - 유효 월이 없으면 Summary는 0.0 반환")
    void calculateIncrease_noValidMonths_returnsZero() {
        // Given: Current Month = 1. Valid Months: none (m < 1)
        UsageHistoryTrendRawDto mockRawData = createMockRawData(1);

        ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(request, "assetId", 10L);
        ReflectionTestUtils.setField(request, "assetName", "Trend-Asset");

        when(trendQueryAdapter.getTrendData(anyInt(), anyInt(), anyLong(), anyString(), anyInt()))
                .thenReturn(mockRawData);

        final LocalDate fixedDate = LocalDate.of(FIXED_YEAR, 1, 1); // Current Month = 1

        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now).thenReturn(fixedDate);

            // When
            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);
            UsageIncreaseSummary summary = result.getSummary();

            // Then
            assertThat(summary.getUsageRateIncrease()).isEqualTo(0.0);
            assertThat(summary.getActualUsageIncrease()).isEqualTo(0.0);
            assertThat(summary.getResourceUtilizationIncrease()).isEqualTo(0.0);
        }
    }
}
