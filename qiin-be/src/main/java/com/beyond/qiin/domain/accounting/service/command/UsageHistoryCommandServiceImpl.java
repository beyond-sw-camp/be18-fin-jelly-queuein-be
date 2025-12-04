package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.accounting.repository.UsageHistoryJpaRepository;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsageHistoryCommandServiceImpl implements UsageHistoryCommandService {

    private final UsageHistoryJpaRepository usageHistoryJpaRepository;
    private final UserHistoryCommandService userHistoryCommandService;
    private final SettlementCommandService settlementCommandService;

    @Override
    @Transactional
    public UsageHistory createUsageHistory(Asset asset, Reservation reservation) {

        int usageTime = calculateUsageTime(reservation.getStartAt(), reservation.getEndAt());
        int actualUsageTime = calculateActualUsageTime(reservation.getActualStartAt(), reservation.getActualEndAt());
        BigDecimal usageRatio = calculateUsageRatio(usageTime, actualUsageTime);

        // 1) UsageHistory 생성 & 저장
        UsageHistory history = UsageHistory.create(asset, reservation, usageTime, actualUsageTime, usageRatio);
        UsageHistory savedHistory = usageHistoryJpaRepository.save(history);

        // 2) UserHistory 생성
        userHistoryCommandService.createUserHistories(reservation, savedHistory);

        // 3) Settlement 생성
        settlementCommandService.createSettlement(savedHistory);

        return savedHistory;
    }

    // 계산 로직
    private int calculateUsageTime(Instant startAt, Instant endAt) {
        return (int) Duration.between(startAt, endAt).toMinutes();
    }

    private int calculateActualUsageTime(Instant actualStartAt, Instant actualEndAt) {
        if (actualStartAt == null || actualEndAt == null) {
            return 0;
        }
        return (int) Duration.between(actualStartAt, actualEndAt).toMinutes();
    }

    private BigDecimal calculateUsageRatio(int usageTime, int actualUsageTime) {
        if (usageTime <= 0 || actualUsageTime <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(actualUsageTime).divide(BigDecimal.valueOf(usageTime), 3, RoundingMode.HALF_UP);
    }
}
