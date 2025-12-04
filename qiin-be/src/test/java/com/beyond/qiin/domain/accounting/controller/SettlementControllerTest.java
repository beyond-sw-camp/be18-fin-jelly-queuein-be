package com.beyond.qiin.domain.accounting.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.accounting.dto.common.ReportingComparisonRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.request.SettlementQuarterRequestDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementPerformanceResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.SettlementQuarterResponseDto;
import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementQuarterRowDto;
import com.beyond.qiin.domain.accounting.service.query.SettlementPerformanceQueryService;
import com.beyond.qiin.domain.accounting.service.query.SettlementQuarterQueryService;
import com.beyond.qiin.domain.accounting.util.SettlementExcelWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("SettlementController 단위 테스트 (Mockito Direct Call)")
class SettlementControllerTest {

    private SettlementController controller;
    private SettlementPerformanceQueryService performanceService;
    private SettlementQuarterQueryService quarterQueryService;
    private SettlementExcelWriter excelWriter;
    private HttpServletResponse response;

    private final int currentYear = LocalDate.now().getYear();

    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
        performanceService = mock(SettlementPerformanceQueryService.class);
        quarterQueryService = mock(SettlementQuarterQueryService.class);
        excelWriter = mock(SettlementExcelWriter.class);
        response = mock(HttpServletResponse.class);

        // Controller 생성자에 Mock 주입
        controller = new SettlementController(performanceService, quarterQueryService, excelWriter);
    }

    // -----------------------------------------------
    // 1. Settlement Performance
    // -----------------------------------------------

    @Test
    @DisplayName("settlementPerformance - 성공")
    void settlementPerformance_success() {
        // Given
        ReportingComparisonRequestDto request = new ReportingComparisonRequestDto();
        ReflectionTestUtils.setField(request, "baseYear", 2024);
        ReflectionTestUtils.setField(request, "compareYear", 2025);

        SettlementPerformanceResponseDto mockResponse = SettlementPerformanceResponseDto.builder()
                .yearRange(SettlementPerformanceResponseDto.YearRangeInfo.builder()
                        .baseYear(2024)
                        .compareYear(2025)
                        .months(12)
                        .build())
                .summary(SettlementPerformanceResponseDto.PerformanceSummary.builder()
                        .accumulatedSaving(new BigDecimal("100000"))
                        .build())
                .build();

        when(performanceService.getPerformance(any(ReportingComparisonRequestDto.class)))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<SettlementPerformanceResponseDto> result = controller.settlementPerformance(request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getYearRange().getBaseYear()).isEqualTo(2024);
        assertThat(result.getBody().getSummary().getAccumulatedSaving()).isEqualByComparingTo("100000");

        // Verify
        verify(performanceService).getPerformance(request);
    }

    // -----------------------------------------------
    // 2. Settlement Quarter
    // -----------------------------------------------

    @Test
    @DisplayName("quarter - year 생략 시 현재 연도 사용 및 성공")
    void quarter_NoYear_usesCurrentYear_success() {
        // Given
        SettlementQuarterRequestDto request = new SettlementQuarterRequestDto();
        ReflectionTestUtils.setField(request, "quarter", 1);
        // year가 null로 설정됨

        SettlementQuarterResponseDto mockResponse = SettlementQuarterResponseDto.builder()
                .year(currentYear)
                .quarter(1)
                .rows(List.of())
                .build();

        when(quarterQueryService.getQuarter(any(SettlementQuarterRequestDto.class)))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<?> result = controller.quarter(request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Controller 내부에서 year가 설정되었는지 확인
        assertThat(((SettlementQuarterResponseDto) result.getBody()).getYear()).isEqualTo(currentYear);

        // Verify: year가 현재 연도로 설정된 request가 전달되었는지 확인
        verify(quarterQueryService)
                .getQuarter(argThat(req ->
                        req.getYear().equals(currentYear) && req.getQuarter().equals(1)));
    }

    @Test
    @DisplayName("quarter - year 지정 시 성공")
    void quarter_WithYear_success() {
        // Given
        int testYear = 2023;
        SettlementQuarterRequestDto request = new SettlementQuarterRequestDto();
        ReflectionTestUtils.setField(request, "year", testYear);
        ReflectionTestUtils.setField(request, "quarter", 4);

        SettlementQuarterResponseDto mockResponse = SettlementQuarterResponseDto.builder()
                .year(testYear)
                .quarter(4)
                .rows(List.of())
                .build();

        when(quarterQueryService.getQuarter(any(SettlementQuarterRequestDto.class)))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<?> result = controller.quarter(request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((SettlementQuarterResponseDto) result.getBody()).getYear()).isEqualTo(testYear);

        // Verify
        verify(quarterQueryService).getQuarter(request);
    }

    // -----------------------------------------------
    // 3. Excel Download
    // -----------------------------------------------

    @Test
    @DisplayName("downloadQuarterExcel - 성공 및 writer 호출 검증")
    void downloadQuarterExcel_success_verifiesWriterCall() {
        // Given
        SettlementQuarterRequestDto request = new SettlementQuarterRequestDto();
        ReflectionTestUtils.setField(request, "quarter", 2);
        // year가 null로 설정됨 (currentYear가 사용될 예정)

        SettlementQuarterRowDto mockRow = SettlementQuarterRowDto.builder()
                .assetId(1L)
                .assetName("AssetX")
                .reservedHours(100)
                .build();
        List<SettlementQuarterRowDto> mockRows = List.of(mockRow);

        SettlementQuarterResponseDto mockResponse = SettlementQuarterResponseDto.builder()
                .year(currentYear)
                .quarter(2)
                .rows(mockRows)
                .build();

        when(quarterQueryService.getQuarter(any(SettlementQuarterRequestDto.class)))
                .thenReturn(mockResponse);

        // When
        controller.downloadQuarterExcel(request, response);

        // Then (응답 코드는 void 타입이므로 검증할 수 없으며, 내부 로직 검증에 집중)

        // 1. QueryService가 현재 연도를 사용하여 호출되었는지 확인
        verify(quarterQueryService)
                .getQuarter(argThat(req ->
                        req.getYear().equals(currentYear) && req.getQuarter().equals(2)));

        // 2. ExcelWriter가 반환된 rows와 HttpServletResponse 객체를 가지고 호출되었는지 확인
        verify(excelWriter).writeFromQuarterRows(eq(response), eq(mockRows));
    }
}
