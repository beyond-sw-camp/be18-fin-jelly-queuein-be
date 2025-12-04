package com.beyond.qiin.domain.accounting.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.accounting.dto.settlement.request.SettlementQuarterRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementQuarterResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementQuarterRowDto;
import com.beyond.qiin.domain.accounting.repository.querydsl.SettlementQuarterQueryAdapter;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("SettlementQuarterQueryServiceImpl 단위 테스트")
class SettlementQuarterQueryServiceImplTest {

    private SettlementQuarterQueryServiceImpl service;
    private SettlementQuarterQueryAdapter settlementQuarterQueryAdapter;

    private final double TOLERANCE = 0.001; // 소수점 셋째 자리까지 검증 (오차 0.001)

    @BeforeEach
    void setUp() {
        settlementQuarterQueryAdapter = mock(SettlementQuarterQueryAdapter.class);
        service = new SettlementQuarterQueryServiceImpl(settlementQuarterQueryAdapter);
    }

    /**
     * Helper: Raw DTO 생성
     */
    private SettlementQuarterRowDto createRawDto(
            Long id, int year, Integer quarter, int reservedHours, int actualHours) {
        return SettlementQuarterRowDto.builder()
                .assetId(id)
                .assetName("Asset-" + id)
                .year(year)
                .quarter(quarter)
                .reservedHours(reservedHours)
                .actualHours(actualHours)
                // 코스트 필드 nz() 유틸리티 테스트용
                .totalUsageCost(null)
                .actualUsageCost(new BigDecimal("5000.5"))
                .usageGapCost(null)
                .build();
    }

    // -----------------------------------------------
    // 1. 핵심 계산 로직 검증 (활용률, 성과율, 등급)
    // -----------------------------------------------

    @Test
    @DisplayName("getQuarter - 일반 연도(2025) 2분기 계산 및 등급 부여 검증")
    void getQuarter_standardCalculations_success() {
        // Given
        int year = 2025;
        int quarter = 2; // 91일, 2184시간
        int reserved = 1800;
        int actual = 1500; // Perform Rate: 1500/1800 ≈ 0.8333...

        SettlementQuarterRequestDto req = new SettlementQuarterRequestDto();
        ReflectionTestUtils.setField(req, "year", year);
        ReflectionTestUtils.setField(req, "quarter", quarter);

        SettlementQuarterRowDto rawDto = createRawDto(1L, year, quarter, reserved, actual);
        List<SettlementQuarterRowDto> mockRows = List.of(rawDto);

        // Mocking 설정 (any() 사용하여 null 인자 충돌 방지)
        when(settlementQuarterQueryAdapter.getQuarterRows(eq(year), eq(quarter), any()))
                .thenReturn(mockRows);

        // When
        SettlementQuarterResponseDto result = service.getQuarter(req);

        // Then
        assertThat(result.getRows()).hasSize(1);
        SettlementQuarterRowDto calculatedDto = result.getRows().getFirst();

        // 성과율 및 등급 검증 (소수점 정밀도 오류 해결)
        assertThat(calculatedDto.getPerformRate()).isCloseTo(0.833, offset(TOLERANCE));
        assertThat(calculatedDto.getPerformGrade()).isEqualTo("A");

        // 활용률 및 등급 검증 (소수점 정밀도 오류 해결)
        assertThat(calculatedDto.getUtilizationRate()).isCloseTo(0.824, offset(TOLERANCE));
        assertThat(calculatedDto.getUtilizationGrade()).isEqualTo("A");

        // nz() 유틸리티 테스트
        assertThat(calculatedDto.getTotalUsageCost()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(settlementQuarterQueryAdapter).getQuarterRows(eq(year), eq(quarter), any());
    }

    @Test
    @DisplayName("getQuarter - 윤년(2024) 1분기 계산 및 C등급 검증")
    void getQuarter_leapYearQ1_gradeC() {
        // Given
        int year = 2024; // 윤년 (91일, 2184시간)
        int quarter = 1;
        int reserved = 500;
        int actual = 200; // Perform Rate: 200/500 = 0.400

        SettlementQuarterRequestDto req = new SettlementQuarterRequestDto();
        ReflectionTestUtils.setField(req, "year", year);
        ReflectionTestUtils.setField(req, "quarter", quarter);

        SettlementQuarterRowDto rawDto = createRawDto(2L, year, quarter, reserved, actual);
        List<SettlementQuarterRowDto> mockRows = List.of(rawDto);

        when(settlementQuarterQueryAdapter.getQuarterRows(eq(year), eq(quarter), any()))
                .thenReturn(mockRows);

        // When
        SettlementQuarterResponseDto result = service.getQuarter(req);

        // Then
        assertThat(result.getRows()).hasSize(1);
        SettlementQuarterRowDto calculatedDto = result.getRows().getFirst();

        // 비율 검증 (소수점 정밀도 오류 해결)
        assertThat(calculatedDto.getUtilizationRate()).isCloseTo(0.229, offset(TOLERANCE));
        assertThat(calculatedDto.getPerformRate()).isCloseTo(0.400, offset(TOLERANCE));
        assertThat(calculatedDto.getUtilizationGrade()).isEqualTo("C");
    }

    @Test
    @DisplayName("getQuarter - 예약 시간이 0일 경우 모든 비율은 0, 등급은 C로 설정")
    void getQuarter_reservedZero_returnsZero() {
        // Given
        int year = 2025;
        int quarter = 4;
        int reserved = 0;
        int actual = 50;

        SettlementQuarterRequestDto req = new SettlementQuarterRequestDto();
        ReflectionTestUtils.setField(req, "year", year);
        ReflectionTestUtils.setField(req, "quarter", quarter);

        SettlementQuarterRowDto rawDto = createRawDto(3L, year, quarter, reserved, actual);
        List<SettlementQuarterRowDto> mockRows = List.of(rawDto);

        when(settlementQuarterQueryAdapter.getQuarterRows(eq(year), eq(quarter), any()))
                .thenReturn(mockRows);

        // When
        SettlementQuarterResponseDto result = service.getQuarter(req);

        // Then
        assertThat(result.getRows()).hasSize(1);
        SettlementQuarterRowDto calculatedDto = result.getRows().getFirst();

        // 비율 검증
        assertThat(calculatedDto.getPerformRate()).isEqualTo(0.0);
        assertThat(calculatedDto.getUtilizationRate()).isEqualTo(0.0);
        assertThat(calculatedDto.getPerformGrade()).isEqualTo("C");
    }
}
