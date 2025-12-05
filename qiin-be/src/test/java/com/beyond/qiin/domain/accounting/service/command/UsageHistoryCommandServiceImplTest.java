package com.beyond.qiin.domain.accounting.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.accounting.repository.UsageHistoryJpaRepository;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UsageHistoryCommandServiceImpl 단위 테스트")
class UsageHistoryCommandServiceImplTest {

    private UsageHistoryCommandServiceImpl service;

    private UsageHistoryJpaRepository usageHistoryJpaRepository;
    private UserHistoryCommandService userHistoryCommandService;
    private SettlementCommandService settlementCommandService;

    // Mock 엔티티
    private Asset mockAsset;
    private Reservation mockReservation;

    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
        usageHistoryJpaRepository = mock(UsageHistoryJpaRepository.class);
        userHistoryCommandService = mock(UserHistoryCommandService.class);
        settlementCommandService = mock(SettlementCommandService.class);

        // Service 생성자에 Mock 주입
        service = new UsageHistoryCommandServiceImpl(
                usageHistoryJpaRepository, userHistoryCommandService, settlementCommandService);

        // 기본 Mock 엔티티 설정
        mockAsset = mock(Asset.class);
        mockReservation = mock(Reservation.class);

        // Mock 객체가 UsageHistory.create()에서 사용될 때를 대비하여 필수 Instant 값 설정
        when(mockReservation.getStartAt()).thenReturn(Instant.parse("2025-10-27T10:00:00Z"));
        when(mockReservation.getEndAt()).thenReturn(Instant.parse("2025-10-27T12:00:00Z"));
        when(mockReservation.getActualStartAt()).thenReturn(Instant.parse("2025-10-27T10:05:00Z"));
        when(mockReservation.getActualEndAt()).thenReturn(Instant.parse("2025-10-27T11:55:00Z"));
    }

    // -----------------------------------------------
    // 1. 성공 케이스 (정상 사용)
    // -----------------------------------------------

    @Test
    @DisplayName("createUsageHistory - 정상적인 예약 및 사용 시 성공 및 종속성 호출 검증")
    void createUsageHistory_normalUse_success() {
        // Given
        Instant reservedStart = Instant.parse("2025-10-27T10:00:00Z");
        Instant reservedEnd = Instant.parse("2025-10-27T12:00:00Z"); // 예약 시간: 120분
        Instant actualStart = Instant.parse("2025-10-27T10:05:00Z");
        Instant actualEnd = Instant.parse("2025-10-27T11:55:00Z"); // 실제 사용 시간: 110분

        when(mockReservation.getStartAt()).thenReturn(reservedStart);
        when(mockReservation.getEndAt()).thenReturn(reservedEnd);
        when(mockReservation.getActualStartAt()).thenReturn(actualStart);
        when(mockReservation.getActualEndAt()).thenReturn(actualEnd);

        // UsageHistoryJpaRepository.save() 시 반환될 Mock 객체 설정 (저장 후 객체)
        UsageHistory mockSavedHistory = mock(UsageHistory.class);
        when(usageHistoryJpaRepository.save(any(UsageHistory.class))).thenReturn(mockSavedHistory);

        // When
        UsageHistory result = service.createUsageHistory(mockAsset, mockReservation);

        // Then
        assertThat(result).isEqualTo(mockSavedHistory);

        // 2. UsageHistory.create()의 인자가 정확한지 검증
        // 120분, 110분, 110/120 = 0.91666... -> 0.917
        verify(usageHistoryJpaRepository)
                .save(argThat(history -> history.getUsageTime() == 120
                        && history.getActualUsageTime() == 110
                        &&
                        // BigDecimal 비교는 compareTo()를 사용
                        history.getUsageRatio().compareTo(new BigDecimal("0.917").setScale(3, RoundingMode.HALF_UP))
                                == 0));

        // 3. 종속성 Command Service들이 호출되었는지 검증
        verify(userHistoryCommandService).createUserHistories(mockReservation, mockSavedHistory);
        verify(settlementCommandService).createSettlement(mockSavedHistory);
    }

    // -----------------------------------------------
    // 2. 실제 사용 시간/비율 계산 로직 테스트 (null/zero case)
    // -----------------------------------------------

    @Test
    @DisplayName("createUsageHistory - 실제 사용 시간이 null일 경우 0분, 비율 0 반환")
    void createUsageHistory_actualTimeNull_returnsZero() {
        // Given: 예약은 120분
        when(mockReservation.getActualStartAt()).thenReturn(null);
        when(mockReservation.getActualEndAt()).thenReturn(null);

        UsageHistory mockSavedHistory = mock(UsageHistory.class);
        when(usageHistoryJpaRepository.save(any(UsageHistory.class))).thenReturn(mockSavedHistory);

        // When
        service.createUsageHistory(mockAsset, mockReservation);

        // Then
        // 실제 사용 시간: 0분, 사용 비율: 0
        verify(usageHistoryJpaRepository)
                .save(argThat(history -> history.getUsageTime() == 120
                        && history.getActualUsageTime() == 0
                        && history.getUsageRatio().compareTo(BigDecimal.ZERO) == 0));

        // 종속성 호출 검증 (정상)
        verify(userHistoryCommandService).createUserHistories(any(), any());
        verify(settlementCommandService).createSettlement(any());
    }

    @Test
    @DisplayName("createUsageHistory - 예약 시간이 0분일 경우 비율 0 반환")
    void createUsageHistory_reservedTimeZero_returnsZero() {
        // Given: 예약 시작/종료 시간 동일 -> 예약 시간 0분
        Instant now = Instant.now();
        when(mockReservation.getStartAt()).thenReturn(now);
        when(mockReservation.getEndAt()).thenReturn(now);

        // 실제 사용 시간은 존재함 (Duration.between으로 인해 Instant.now()는 동일하지 않아 Duration이 0이 아닐 수 있으므로 임의의 값으로 설정)
        when(mockReservation.getActualStartAt()).thenReturn(now.minusSeconds(3000));
        when(mockReservation.getActualEndAt()).thenReturn(now);

        UsageHistory mockSavedHistory = mock(UsageHistory.class);
        when(usageHistoryJpaRepository.save(any(UsageHistory.class))).thenReturn(mockSavedHistory);

        // When
        service.createUsageHistory(mockAsset, mockReservation);

        // Then
        // 예약 시간이 0이므로 비율은 0 (calculateUsageRatio 로직 검증)
        verify(usageHistoryJpaRepository)
                .save(argThat(history ->
                        history.getUsageTime() == 0 && history.getUsageRatio().compareTo(BigDecimal.ZERO) == 0));

        verify(settlementCommandService).createSettlement(any());
    }
}
