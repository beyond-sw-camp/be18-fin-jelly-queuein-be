package com.beyond.qiin.domain.accounting.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryTrendResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendPopularCountDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendPopularTimeDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.raw.UsageHistoryTrendRawDto.UsageAggregate;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryTrendQueryAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendRedisAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendTopRedisAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UsageHistoryTrendQueryServiceImpl 단위 테스트")
class UsageHistoryTrendQueryServiceImplTest {

    private UsageHistoryTrendQueryServiceImpl service;
    private UsageHistoryTrendQueryAdapter trendQueryAdapter;
    private UsageTrendRedisAdapter redisAdapter;
    private UsageTrendTopRedisAdapter topRedisAdapter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        trendQueryAdapter = mock(UsageHistoryTrendQueryAdapter.class);
        redisAdapter = mock(UsageTrendRedisAdapter.class);
        topRedisAdapter = mock(UsageTrendTopRedisAdapter.class);

        // Set up Redis mock behavior (기본: 캐시 없음)
        when(redisAdapter.get(anyString())).thenReturn(null);
        doNothing().when(redisAdapter).save(anyString(), anyDouble(), any());

        // Set up Top Redis mock behavior (기본: 캐시 없음)
        when(topRedisAdapter.get(anyString())).thenReturn(null);
        doNothing().when(topRedisAdapter).save(anyString(), anyString(), any());

        // Initialize the service
        service = new UsageHistoryTrendQueryServiceImpl(trendQueryAdapter, redisAdapter, topRedisAdapter);
        // ObjectMapper 주입
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
    }

    /**
     * Helper: 월별 사용량 Raw 데이터 Mock 생성
     */
    private UsageHistoryTrendRawDto mockRaw() {
        return UsageHistoryTrendRawDto.builder()
                .assetId(10L)
                .assetName("Test-Asset")
                // Base Year (2024년)
                .baseYearData(Map.of(
                        1,
                                UsageAggregate.builder()
                                        .reservedUsage(1000)
                                        .actualUsage(800)
                                        .build(), // 80.0%
                        2,
                                UsageAggregate.builder()
                                        .reservedUsage(1000)
                                        .actualUsage(500)
                                        .build() // 50.0%
                        ))
                // Compare Year (2025년)
                .compareYearData(Map.of(
                        1,
                                UsageAggregate.builder()
                                        .reservedUsage(1000)
                                        .actualUsage(900)
                                        .build(), // 90.0%
                        2,
                                UsageAggregate.builder()
                                        .reservedUsage(1200)
                                        .actualUsage(720)
                                        .build() // 60.0%
                        ))
                .build();
    }

    @Test
    @DisplayName("getUsageHistoryTrend - 월별 사용률 계산 및 리스트 구성 성공 (누락 월 포함)")
    void getUsageHistoryTrend_calculatesMonthlyRatesCorrectly() {
        // Given
        int baseYear = 2024;
        int compareYear = 2025;
        int fixedYear = 2025;
        int fixedMonth = 6;

        when(trendQueryAdapter.getTrendData(baseYear, compareYear, "Test-Asset"))
                .thenReturn(mockRaw());

        // ⭐ 변경된 부분: LocalDate 객체를 when 블록 외부에서 미리 생성하여 Mocking 충돌을 방지합니다.
        LocalDate fixedDate = LocalDate.of(fixedYear, fixedMonth, 1);

        try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedDate);

            ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
            ReflectionTestUtils.setField(request, "assetName", "Test-Asset");
            ReflectionTestUtils.setField(request, "baseYear", baseYear);
            ReflectionTestUtils.setField(request, "compareYear", compareYear);

            // When
            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);

            List<UsageHistoryTrendResponseDto.MonthlyUsageData> list = result.getMonthlyData();

            // Then
            assertThat(list).hasSize(12);

            // 1월 검증 (Base: 80.0%, Compare: 90.0%)
            assertThat(list.get(0).getMonth()).isEqualTo(1);
            assertThat(list.get(0).getBaseYearUsageRate()).isEqualTo(80.0);
            assertThat(list.get(0).getCompareYearUsageRate()).isEqualTo(90.0);

            // 2월 검증 (Base: 50.0%, Compare: 60.0%)
            assertThat(list.get(1).getMonth()).isEqualTo(2);
            assertThat(list.get(1).getBaseYearUsageRate()).isEqualTo(50.0);
            assertThat(list.get(1).getCompareYearUsageRate()).isEqualTo(60.0);

            // 3월 검증 (데이터가 없으므로 0.0%로 채워졌는지 확인)
            assertThat(list.get(2).getMonth()).isEqualTo(3);
            assertThat(list.get(2).getBaseYearUsageRate()).isEqualTo(0.0);
            assertThat(list.get(2).getCompareYearUsageRate()).isEqualTo(0.0);

            // Verify Redis calls
            verify(redisAdapter, times(1)).save(eq("usageTrend:2024:1:id-10"), eq(80.0), any());
            verify(redisAdapter, times(1)).save(eq("usageTrend:2024:2:id-10"), eq(50.0), any());
            verify(redisAdapter, times(1)).save(eq("usageTrend:2025:1:id-10"), eq(90.0), any());
            verify(redisAdapter, times(1)).save(eq("usageTrend:2025:2:id-10"), eq(60.0), any());
        }
    }

    @Test
    @DisplayName("getUsageHistoryTrend - 실제 사용률 증가 계산 성공")
    void getUsageHistoryTrend_calculatesActualUsageIncreaseCorrectly() {
        // Given
        int baseYear = 2024;
        int compareYear = 2025;
        int fixedYear = 2025;
        int fixedMonth = 6;

        when(trendQueryAdapter.getTrendData(baseYear, compareYear, "Test-Asset"))
                .thenReturn(mockRaw());

        // Expected Calculation: 8.6
        final double expectedIncrease = 8.6;

        // ⭐ 변경된 부분: LocalDate 객체를 when 블록 외부에서 미리 생성합니다.
        LocalDate fixedDate = LocalDate.of(fixedYear, fixedMonth, 1);

        try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedDate);

            ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
            ReflectionTestUtils.setField(request, "assetName", "Test-Asset");
            ReflectionTestUtils.setField(request, "baseYear", baseYear);
            ReflectionTestUtils.setField(request, "compareYear", compareYear);

            // When
            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);

            // Then
            assertThat(result.getActualUsageIncrease()).isEqualTo(expectedIncrease);
        }
    }

    @Test
    @DisplayName("getUsageHistoryTrend - TOP3 조회 로직 호출 및 캐싱 로직 확인")
    void getUsageHistoryTrend_Top3CallsAndCachingLogicVerification() throws Exception {
        // Given
        int baseYear = 2024;
        int compareYear = 2025;
        int fixedYear = 2025;
        int fixedMonth = 6;

        // TOP 데이터 Mock
        List<UsageHistoryTrendPopularCountDto> countTop = List.of(new UsageHistoryTrendPopularCountDto(1L, "A", 100));
        List<UsageHistoryTrendPopularTimeDto> timeTop = List.of(new UsageHistoryTrendPopularTimeDto(2L, "B", 6000));

        // Raw Data Mock
        when(trendQueryAdapter.getTrendData(anyInt(), anyInt(), anyString())).thenReturn(mockRaw());

        // Adapter Mock (DB 조회 시 Mock 데이터 반환)
        when(trendQueryAdapter.getTopByCount(eq(baseYear))).thenReturn(countTop);
        when(trendQueryAdapter.getTopByCount(eq(compareYear))).thenReturn(List.of());
        when(trendQueryAdapter.getTopByTime(eq(baseYear))).thenReturn(timeTop);
        when(trendQueryAdapter.getTopByTime(eq(compareYear))).thenReturn(List.of());

        // ⭐ 변경된 부분: LocalDate 객체를 when 블록 외부에서 미리 생성합니다.
        LocalDate fixedDate = LocalDate.of(fixedYear, fixedMonth, 1);

        try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedDate);

            ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
            ReflectionTestUtils.setField(request, "assetName", "Test-Asset");
            ReflectionTestUtils.setField(request, "baseYear", baseYear);
            ReflectionTestUtils.setField(request, "compareYear", compareYear);

            // When
            UsageHistoryTrendResponseDto result = service.getUsageHistoryTrend(request);

            // Then
            assertThat(result.getPopularByCount().getBaseYear()).hasSize(1);
            assertThat(result.getPopularByTime().getBaseYear().get(0).getTotalMinutes())
                    .isEqualTo(6000);

            // 1. DB 조회 검증
            verify(trendQueryAdapter, times(1)).getTopByCount(eq(baseYear));
            verify(trendQueryAdapter, times(1)).getTopByTime(eq(baseYear));
            verify(trendQueryAdapter, times(1)).getTopByCount(eq(compareYear));
            verify(trendQueryAdapter, times(1)).getTopByTime(eq(compareYear));

            // 2. Redis 캐시 저장 검증
            String expectedCountJson =
                    objectMapper.writeValueAsString(result.getPopularByCount().getBaseYear());
            String expectedTimeJson =
                    objectMapper.writeValueAsString(result.getPopularByTime().getBaseYear());

            verify(topRedisAdapter, times(1)).save(eq("usageTrendTop:2024:count"), eq(expectedCountJson), isNull());
            verify(topRedisAdapter, times(1)).save(eq("usageTrendTop:2024:time"), eq(expectedTimeJson), isNull());

            // Compare Year (2025)는 현재 연도이므로 캐시 저장이 일어나지 않아야 함
            verify(topRedisAdapter, never()).save(eq("usageTrendTop:2025:count"), anyString(), any());
            verify(topRedisAdapter, never()).save(eq("usageTrendTop:2025:time"), anyString(), any());
        }
    }
}
