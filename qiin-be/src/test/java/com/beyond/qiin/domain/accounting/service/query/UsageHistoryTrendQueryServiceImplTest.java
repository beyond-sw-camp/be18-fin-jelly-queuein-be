package com.beyond.qiin.domain.accounting.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryTrendQueryAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendRedisAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendTopRedisAdapter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

class UsageHistoryTrendQueryServiceImplTest {

    private UsageHistoryTrendQueryServiceImpl service;
    private UsageHistoryTrendQueryAdapter trendQueryAdapter;
    private UsageTrendRedisAdapter redisAdapter;
    private UsageTrendTopRedisAdapter topRedisAdapter;

    private final int FIXED_YEAR = 2025;
    private final int FIXED_MONTH = 6;

    @BeforeEach
    void setUp() {
        trendQueryAdapter = mock(UsageHistoryTrendQueryAdapter.class);
        redisAdapter = mock(UsageTrendRedisAdapter.class);
        topRedisAdapter = mock(UsageTrendTopRedisAdapter.class);

        when(redisAdapter.get(anyString())).thenReturn(null);
        doNothing().when(redisAdapter).save(anyString(), anyDouble(), any());

        when(topRedisAdapter.get(anyString())).thenReturn(null);
        doNothing().when(topRedisAdapter).save(anyString(), anyString(), any());

        service = new UsageHistoryTrendQueryServiceImpl(trendQueryAdapter, redisAdapter, topRedisAdapter);
    }

    private UsageHistoryTrendRawDto mockRaw() {
        return UsageHistoryTrendRawDto.builder()
                .assetId(10L)
                .assetName("Test-Asset")
                .baseYearData(Map.of(
                        1,
                                UsageAggregate.builder()
                                        .reservedUsage(1000)
                                        .actualUsage(800)
                                        .build(),
                        2,
                                UsageAggregate.builder()
                                        .reservedUsage(1000)
                                        .actualUsage(500)
                                        .build()))
                .compareYearData(Map.of(
                        1,
                                UsageAggregate.builder()
                                        .reservedUsage(1000)
                                        .actualUsage(900)
                                        .build(),
                        2,
                                UsageAggregate.builder()
                                        .reservedUsage(1200)
                                        .actualUsage(720)
                                        .build()))
                .build();
    }

    @Test
    @DisplayName("월별 사용률 계산 테스트")
    void testMonthlyUsageRate() {

        int baseYear = FIXED_YEAR - 1;
        int compareYear = FIXED_YEAR;

        when(trendQueryAdapter.getTrendData(baseYear, compareYear, "Test-Asset"))
                .thenReturn(mockRaw());

        try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(LocalDate.of(FIXED_YEAR, FIXED_MONTH, 1));

            ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
            ReflectionTestUtils.setField(request, "assetName", "Test-Asset");

            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);

            List<UsageHistoryTrendResponseDto.MonthlyUsageData> list = result.getMonthlyData();

            assertThat(list).hasSize(12);

            assertThat(list.get(0).getBaseYearUsageRate()).isEqualTo(80.0);
            assertThat(list.get(0).getCompareYearUsageRate()).isEqualTo(90.0);
        }
    }

    @Test
    @DisplayName("실제 사용률 증가 계산 테스트")
    void testActualUsageIncrease() {

        int baseYear = 2024;
        int compareYear = 2025;

        when(trendQueryAdapter.getTrendData(baseYear, compareYear, "Test-Asset"))
                .thenReturn(mockRaw());

        try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(LocalDate.of(2025, 6, 1));

            ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
            ReflectionTestUtils.setField(request, "assetName", "Test-Asset");

            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);

            assertThat(result.getActualUsageIncrease()).isGreaterThan(0.0);
        }
    }

    @Test
    @DisplayName("TOP3 조회 로직 호출 테스트")
    void testTop3Call() {

        int baseYear = 2024;
        int compareYear = 2025;

        when(trendQueryAdapter.getTrendData(baseYear, compareYear, "Test-Asset"))
                .thenReturn(mockRaw());

        when(trendQueryAdapter.getTopByCount(anyInt())).thenReturn(List.of());
        when(trendQueryAdapter.getTopByTime(anyInt())).thenReturn(List.of());

        try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(LocalDate.of(2025, 6, 1));

            ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
            ReflectionTestUtils.setField(request, "assetName", "Test-Asset");

            service.getUsageHistoryTrend(request);

            verify(trendQueryAdapter, times(1)).getTopByCount(baseYear);
            verify(trendQueryAdapter, times(1)).getTopByCount(compareYear);

            verify(trendQueryAdapter, times(1)).getTopByTime(baseYear);
            verify(trendQueryAdapter, times(1)).getTopByTime(compareYear);
        }
    }
}
