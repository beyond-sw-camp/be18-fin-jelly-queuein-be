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
        redisAdapter = mock(SettlementPerformanceMonthRedisAdapter.class); // Redis Mock 설정
        service = new SettlementPerformanceQueryServiceImpl(queryAdapter, redisAdapter); // 의존성 주입

        // Redis에서 값이 없을 경우 null 반환
        when(redisAdapter.get(anyString())).thenReturn(null);

        // Redis 저장 시 아무 작업도 하지 않음
        doNothing().when(redisAdapter).save(anyString(), any(BigDecimal.class), eq(null)); // Redis 저장 Mock

        // 기본적으로 DB에서 값을 가져오는 부분을 Mocking
        when(queryAdapter.getMonthlyPerformance(anyInt(), anyInt(), anyLong(), anyString()))
                .thenReturn(createMockRawData()); // Mock 데이터로 반환
        when(queryAdapter.getTotalSavingAllTime()).thenReturn(BigDecimal.ZERO);

        // Mocking: assetId를 "Server-X"에 대해 반환
        when(queryAdapter.getAssetIdByName(eq("Server-X"))).thenReturn(10L); // "Server-X"에 대해 10L을 반환
    }

    /**
     * Helper: Raw 데이터 Mock 생성
     */
    private SettlementPerformanceRawDto createMockRawData() {
        // baseYearData와 compareYearData에 실제 값들 설정
        Map<Integer, BigDecimal> baseData = Map.of(1, new BigDecimal("100.00"), 2, new BigDecimal("-50.00"));
        Map<Integer, BigDecimal> compareData = Map.of(1, new BigDecimal("200.00"), 3, new BigDecimal("150.00"));

        return SettlementPerformanceRawDto.builder()
                .assetId(10L) // 예상되는 assetId 값
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

        ReportingComparisonRequestDto req = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(req, "assetId", 10L); // 10L로 설정하여 예상되는 값에 맞춤
        ReflectionTestUtils.setField(req, "assetName", "Server-X");

        // When
        SettlementPerformanceResponseDto result = service.getPerformance(req);

        // Then
        // Base Year Total: 100.00 + (-50.00) = 50.00
        assertThat(result.getSummary().getBaseYearTotalSaving()).isEqualByComparingTo("50.00");
        assertThat(result.getSummary().getCompareYearCurrentSaving()).isEqualByComparingTo("350.00");

        // Verify
        verify(queryAdapter).getMonthlyPerformance(eq(baseYear), eq(currentYear), eq(10L), eq("Server-X"));
        verify(queryAdapter).getTotalSavingAllTime();
        verify(redisAdapter).get(anyString()); // Redis 캐시 조회 확인
        verify(redisAdapter).save(anyString(), any(BigDecimal.class), eq(null)); // Redis에 데이터 저장 확인
    }

    @Test
    @DisplayName("getPerformance - 요청에 연도가 포함될 경우 해당 연도 사용")
    void getPerformance_withCustomYears_usesCustomYears() {
        // Given
        int customBase = 2020;
        int customCompare = 2021;
        long expectedAssetId = 10L; // 예상되는 assetId 값

        ReportingComparisonRequestDto req = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(req, "baseYear", customBase);
        ReflectionTestUtils.setField(req, "compareYear", customCompare);

        // 테스트를 위해 assetId와 assetName을 모두 요청에 포함시킵니다.
        ReflectionTestUtils.setField(req, "assetId", expectedAssetId); // 10L로 설정 (요청 DTO에 설정)
        ReflectionTestUtils.setField(req, "assetName", "CustomAsset"); // assetName 설정

        // getMonthlyPerformance 내부에서 assetName이 있을 경우 assetId를 갱신하기 위해 getAssetIdByName을 호출합니다.
        when(queryAdapter.getAssetIdByName(eq("CustomAsset"))).thenReturn(expectedAssetId);

        // When
        SettlementPerformanceResponseDto result = service.getPerformance(req);

        // Then
        assertThat(result.getYearRange().getBaseYear()).isEqualTo(customBase);
        assertThat(result.getYearRange().getCompareYear()).isEqualTo(customCompare);
        // assetId가 올바르게 10L로 설정되었는지 확인
        assertThat(result.getAsset().getAssetId()).isEqualTo(expectedAssetId);
        assertThat(result.getAsset().getAssetId()).isEqualTo(10L); // 명시적으로 10L 확인

        // Verify
        // getMonthlyPerformance 호출 시 customBase, customCompare, assetId(10L), assetName("CustomAsset") 사용 확인
        verify(queryAdapter)
                .getMonthlyPerformance(eq(customBase), eq(customCompare), eq(expectedAssetId), eq("CustomAsset"));

        // getAssetIdByName("CustomAsset") 호출이 있었는지 확인
        verify(queryAdapter).getAssetIdByName(eq("CustomAsset"));

        verify(redisAdapter).get(anyString()); // Redis 캐시 조회 확인
    }
}
