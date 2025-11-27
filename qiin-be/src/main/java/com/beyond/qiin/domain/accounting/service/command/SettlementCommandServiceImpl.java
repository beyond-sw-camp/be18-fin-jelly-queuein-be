package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.entity.Settlement;
import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import com.beyond.qiin.domain.accounting.repository.SettlementJpaRepository;
import com.beyond.qiin.domain.accounting.repository.UsageTargetJpaRepository;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementCommandServiceImpl implements SettlementCommandService {

    private final SettlementJpaRepository settlementJpaRepository;
    private final UsageTargetJpaRepository usageTargetJpaRepository;

    @Override
    @Transactional
    public Settlement createSettlement(final UsageHistory history) {

        Asset asset = history.getAsset();

        BigDecimal sixty = BigDecimal.valueOf(60);

        int reservedMinutes = history.getUsageTime();
        int actualMinutes = history.getActualUsageTime();

        BigDecimal costPerHour = asset.getCostPerHour();

        // (1) 예약 기준 시간 → 비용
        BigDecimal reservedHours = BigDecimal.valueOf(reservedMinutes).divide(sixty, 3, RoundingMode.HALF_UP);
        BigDecimal totalUsageCost = reservedHours.multiply(costPerHour);

        // (2) 실제 기준 시간 → 비용
        BigDecimal actualHours = BigDecimal.valueOf(actualMinutes).divide(sixty, 3, RoundingMode.HALF_UP);
        BigDecimal actualUsageCost = actualHours.multiply(costPerHour);

        // (3) 목표 사용률 조회
        int year = history.getActualStartAt()
                .atZone(java.time.ZoneId.systemDefault())
                .getYear();

        UsageTarget usageTarget = usageTargetJpaRepository
                .findByYear(year)
                .orElseThrow(() -> new IllegalStateException("Usage target not found for year: " + year));

        BigDecimal targetRate = usageTarget.getTargetRate(); // 예: 0.8 (80%)

        // (4) 목표 대비 손익 계산
        // 사용률 = 실제사용시간 / 예약사용시간
        BigDecimal actualRate = actualHours.divide(reservedHours, 3, RoundingMode.HALF_UP);

        // 차이(+)면 초과 사용, (-)면 미달
        BigDecimal rateGap = actualRate.subtract(targetRate);

        // 손익 금액 = 예약시간 × (사용률 차이) × 단가
        BigDecimal usageGapCost = reservedHours.multiply(rateGap).multiply(costPerHour);

        // (5) 정산 생성
        Settlement settlement = Settlement.create(
                history, usageTarget.getId(), costPerHour, totalUsageCost, actualUsageCost, usageGapCost);

        return settlementJpaRepository.save(settlement);
    }
}
