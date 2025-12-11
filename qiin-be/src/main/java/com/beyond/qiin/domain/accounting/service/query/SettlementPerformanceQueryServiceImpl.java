package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.common.request.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementPerformanceRawDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementPerformanceQueryAdapter;
import com.beyond.qiin.infra.redis.accounting.settlement.SettlementPerformanceMonthRedisAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementPerformanceQueryServiceImpl implements SettlementPerformanceQueryService {

    private final SettlementPerformanceQueryAdapter queryAdapter;
    private final SettlementPerformanceMonthRedisAdapter redisAdapter;

    @Override
    public SettlementPerformanceResponseDto getPerformance(ReportingComparisonRequestDto req) {

        int currentYear = LocalDate.now().getYear();

        int baseYear = req.getBaseYear() != null ? req.getBaseYear() : currentYear - 1;
        int compareYear = req.getCompareYear() != null ? req.getCompareYear() : currentYear;

        // assetName을 기준으로 assetId를 찾고, Redis 캐시 조회
        String assetName = req.getAssetName();
        Long assetId = resolveAssetIdFromName(assetName); // assetName으로 assetId를 찾음

        // 캐시 키 생성 (assetId 기반으로 캐시 키 생성)
        String key = "settlementPerformance:%s:%d:%d:%s"
                .formatted("base", baseYear, compareYear, assetId != null ? "id-" + assetId : "all");

        // Redis에서 캐시된 값 조회
        BigDecimal cachedValue = redisAdapter.get(key);

        if (cachedValue == null) {
            // 캐시가 없다면 DB에서 성과 데이터를 조회
            SettlementPerformanceRawDto raw =
                    queryAdapter.getMonthlyPerformance(baseYear, compareYear, assetId, assetName);
            Map<Integer, BigDecimal> baseMap = Optional.ofNullable(raw)
                    .map(SettlementPerformanceRawDto::getBaseYearData)
                    .orElse(Collections.emptyMap());

            Map<Integer, BigDecimal> compareMap = Optional.ofNullable(raw)
                    .map(SettlementPerformanceRawDto::getCompareYearData)
                    .orElse(Collections.emptyMap());

            // 월별 성과 리스트 구성
            List<SettlementPerformanceResponseDto.MonthlyPerformance> monthlyList = new ArrayList<>();
            for (int m = 1; m <= 12; m++) {
                monthlyList.add(SettlementPerformanceResponseDto.MonthlyPerformance.builder()
                        .month(m)
                        .baseYearSaving(baseMap.getOrDefault(m, BigDecimal.ZERO))
                        .compareYearSaving(compareMap.getOrDefault(m, BigDecimal.ZERO))
                        .build());
            }

            // 총 절감 금액
            BigDecimal baseYearTotal = sum(baseMap);
            BigDecimal compareYearTotal = sum(compareMap);
            BigDecimal accumulated = queryAdapter.getTotalSavingAllTime();

            // Redis에 성과 데이터 저장
            cachedValue = baseYearTotal; // 이 부분을 필요한 값으로 설정
            redisAdapter.save(key, cachedValue, null); // null은 영구 저장

            // 최종 응답 DTO 생성
            return SettlementPerformanceResponseDto.builder()
                    .asset(new SettlementPerformanceResponseDto.AssetInfo(assetId, assetName))
                    .yearRange(new SettlementPerformanceResponseDto.YearRangeInfo(baseYear, compareYear, 12))
                    .monthlyData(monthlyList)
                    .summary(new SettlementPerformanceResponseDto.PerformanceSummary(
                            baseYearTotal, compareYearTotal, accumulated))
                    .build();
        }

        // 캐시된 값이 있다면 바로 반환
        return SettlementPerformanceResponseDto.builder()
                .asset(new SettlementPerformanceResponseDto.AssetInfo(assetId, assetName))
                .yearRange(new SettlementPerformanceResponseDto.YearRangeInfo(baseYear, compareYear, 12))
                .monthlyData(new ArrayList<>()) // 월별 데이터는 이미 캐시에서 처리됨
                .summary(new SettlementPerformanceResponseDto.PerformanceSummary(
                        cachedValue, BigDecimal.ZERO, BigDecimal.ZERO))
                .build();
    }

    // 총합 계산 (누적 절감 금액)
    private BigDecimal sum(Map<Integer, BigDecimal> map) {
        return map.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // assetName을 기준으로 assetId를 찾기
    private Long resolveAssetIdFromName(String assetName) {
        // 자원명으로 assetId 조회
        return queryAdapter.getAssetIdByName(assetName);
    }
}
