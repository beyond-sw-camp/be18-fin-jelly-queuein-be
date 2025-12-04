package com.beyond.qiin.domain.accounting.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.entity.Settlement;
import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import com.beyond.qiin.domain.accounting.repository.SettlementJpaRepository;
import com.beyond.qiin.domain.accounting.repository.UsageTargetJpaRepository;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SettlementCommandServiceImpl 단위 테스트")
class SettlementCommandServiceImplTest {

    private SettlementCommandServiceImpl service;
    private SettlementJpaRepository settlementJpaRepository;
    private UsageTargetJpaRepository usageTargetJpaRepository;

    @BeforeEach
    void setUp() {
        settlementJpaRepository = mock(SettlementJpaRepository.class);
        usageTargetJpaRepository = mock(UsageTargetJpaRepository.class);
        service = new SettlementCommandServiceImpl(settlementJpaRepository, usageTargetJpaRepository);
    }

    /**
     * Helper: Mock UsageHistory 생성
     */
    private UsageHistory createMockUsageHistory(
            int reservedMinutes, int actualMinutes, BigDecimal costPerHour, int year) {
        // 1. Asset Mock
        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getCostPerHour()).thenReturn(costPerHour);

        // 2. UsageHistory Mock
        UsageHistory mockHistory = mock(UsageHistory.class);
        when(mockHistory.getAsset()).thenReturn(mockAsset);
        when(mockHistory.getUsageTime()).thenReturn(reservedMinutes);
        when(mockHistory.getActualUsageTime()).thenReturn(actualMinutes);

        // Instant Mock (특정 연도를 반환하도록 설정)
        Instant mockStartAt =
                ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant();
        when(mockHistory.getActualStartAt()).thenReturn(mockStartAt);

        return mockHistory;
    }

    /**
     * Helper: Mock UsageTarget 생성
     */
    private UsageTarget createMockUsageTarget(Long id, int year, BigDecimal rate) {
        UsageTarget mockTarget = mock(UsageTarget.class);
        when(mockTarget.getId()).thenReturn(id);
        when(mockTarget.getYear()).thenReturn(year);
        when(mockTarget.getTargetRate()).thenReturn(rate);
        return mockTarget;
    }

    // -----------------------------------------------
    // 1. 성공 케이스: 목표 사용률 달성 (낭비 발생)
    // -----------------------------------------------

    @Test
    @DisplayName("createSettlement - 성과 낭비(초과사용) 계산 성공")
    void createSettlement_overuse_success() {
        // Given
        int testYear = 2025;
        int reservedMinutes = 120; // 2.0 시간
        int actualMinutes = 100; // 1.667 시간
        BigDecimal costPerHour = new BigDecimal("100.00");
        BigDecimal targetRate = new BigDecimal("0.70"); // 목표 사용률 70%

        UsageHistory mockHistory = createMockUsageHistory(reservedMinutes, actualMinutes, costPerHour, testYear);
        UsageTarget mockTarget = createMockUsageTarget(1L, testYear, targetRate);

        when(usageTargetJpaRepository.findByYear(testYear)).thenReturn(Optional.of(mockTarget));
        when(settlementJpaRepository.save(any(Settlement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Expected Usage Gap Cost: 26.800 (낭비/초과 비용)
        BigDecimal expectedUsageGapCost = new BigDecimal("26.800").setScale(3, RoundingMode.HALF_UP);

        // When
        Settlement result = service.createSettlement(mockHistory);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalUsageCost()).isEqualByComparingTo(new BigDecimal("200.000"));
        assertThat(result.getActualUsageCost()).isEqualByComparingTo(new BigDecimal("166.700"));
        assertThat(result.getUsageGapCost()).isEqualByComparingTo(expectedUsageGapCost);

        // Verify
        verify(settlementJpaRepository).save(any(Settlement.class));
    }

    // -----------------------------------------------
    // 2. 성공 케이스: 목표 사용률 미달 (절감 발생)
    // -----------------------------------------------

    @Test
    @DisplayName("createSettlement - 성과 절감(미달사용) 계산 성공")
    void createSettlement_underuse_success() {
        // Given
        int testYear = 2024;
        int reservedMinutes = 180; // 3.0 시간
        int actualMinutes = 90; // 1.5 시간
        BigDecimal costPerHour = new BigDecimal("50.00");
        BigDecimal targetRate = new BigDecimal("0.75"); // 목표 사용률 75%

        UsageHistory mockHistory = createMockUsageHistory(reservedMinutes, actualMinutes, costPerHour, testYear);
        UsageTarget mockTarget = createMockUsageTarget(2L, testYear, targetRate);

        when(usageTargetJpaRepository.findByYear(testYear)).thenReturn(Optional.of(mockTarget));
        when(settlementJpaRepository.save(any(Settlement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Expected Usage Gap Cost: -37.500 (절감 비용)
        BigDecimal expectedUsageGapCost = new BigDecimal("-37.500").setScale(3, RoundingMode.HALF_UP);

        // When
        Settlement result = service.createSettlement(mockHistory);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalUsageCost()).isEqualByComparingTo(new BigDecimal("150.000"));
        assertThat(result.getActualUsageCost()).isEqualByComparingTo(new BigDecimal("75.000"));
        assertThat(result.getUsageGapCost()).isEqualByComparingTo(expectedUsageGapCost);

        // Verify
        verify(settlementJpaRepository).save(any(Settlement.class));
    }

    // -----------------------------------------------
    // 3. 예외 케이스: UsageTarget 미존재
    // -----------------------------------------------

    @Test
    @DisplayName("createSettlement - UsageTarget 없을때 IllegalStateException 발생")
    void createSettlement_targetNotFound_throwsException() {
        // Given
        int testYear = 2026;
        UsageHistory mockHistory = createMockUsageHistory(60, 60, new BigDecimal("10.00"), testYear);

        when(usageTargetJpaRepository.findByYear(testYear)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> service.createSettlement(mockHistory));

        assertThat(exception.getMessage()).contains("Usage target not found for year: " + testYear);

        // Verify: save는 호출되지 않았는지 확인 (이전 오류 해결: never() 구문 수정)
        verify(settlementJpaRepository, never()).save(any(Settlement.class));
    }
}
