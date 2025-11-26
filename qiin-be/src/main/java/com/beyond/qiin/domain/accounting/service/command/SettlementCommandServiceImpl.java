package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.entity.Settlement;
import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.accounting.repository.SettlementJpaRepository;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementCommandServiceImpl implements SettlementCommandService {

    private final SettlementJpaRepository settlementJpaRepository;

    @Override
    public Settlement createSettlement(UsageHistory history) {

        Asset asset = history.getAsset();

        BigDecimal sixty = new BigDecimal("60");

        int usageMinutes = history.getUsageTime(); // 예약 시간(분)
        int actualUsageMinutes = history.getActualUsageTime(); // 실제 사용시간(분)

        BigDecimal costPerHourSnapshot = asset.getCostPerHour(); // 시간당 단가

        // 예약 기준 요금 계산
        BigDecimal usageHours = BigDecimal.valueOf(usageMinutes).divide(sixty, 3, RoundingMode.HALF_UP);

        BigDecimal totalUsageCost = usageHours.multiply(costPerHourSnapshot);

        // 실제 기준 요금 계산

        BigDecimal actualUsageHours = BigDecimal.valueOf(actualUsageMinutes).divide(sixty, 3, RoundingMode.HALF_UP);

        BigDecimal actualUsageCost = actualUsageHours.multiply(costPerHourSnapshot);

        // 🔥 고정비 계산 (시간당 고정비 × 실제 사용시간)
        BigDecimal periodCostPerHour = asset.getPeriodCost(); // 시간당 고정비

        BigDecimal periodCostShare = actualUsageHours.multiply(periodCostPerHour);

        Settlement settlement = Settlement.create(
                history,
                usageMinutes,
                actualUsageMinutes,
                costPerHourSnapshot,
                totalUsageCost,
                actualUsageCost,
                periodCostShare);

        return settlementJpaRepository.save(settlement);
    }
}
