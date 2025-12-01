package com.beyond.qiin.domain.accounting.util;

import com.beyond.qiin.domain.accounting.dto.settlement.response.raw.SettlementQuarterRowDto;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementExcelWriter {

    // 화면 조회용 DTO 그대로 Excel 생성
    public void writeFromQuarterRows(HttpServletResponse response, List<SettlementQuarterRowDto> rows) {
        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("분기정산");

            // =======================
            // 1) 스타일 정의
            // =======================
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.DARK_BLUE.getIndex()); // 글자 색
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex()); // 배경색
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle bodyStyle = wb.createCellStyle();
            Font bodyFont = wb.createFont();
            bodyFont.setColor(IndexedColors.DARK_BLUE.getIndex());
            bodyStyle.setFont(bodyFont);
            bodyStyle.setAlignment(HorizontalAlignment.CENTER);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);

            // =======================
            // 2) 헤더 생성
            // =======================
            String[] headers = {
                "연도", "분기", "자원ID", "자원명", "예약시간", "실사용시간", "활용률", "성과율", "예약금액", "실사용금액", "절감/낭비", "활용등급", "성과등급"
            };

            Row h = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = h.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256); // 열 너비 15로 고정
            }

            // =======================
            // 3) 데이터 채우기
            // =======================
            int rowIdx = 1;
            for (SettlementQuarterRowDto dto : rows) {
                Row r = sheet.createRow(rowIdx++);
                int col = 0;

                r.createCell(col++).setCellValue(dto.getYear());
                r.createCell(col++).setCellValue(dto.getQuarter() != null ? dto.getQuarter() : 0);
                r.createCell(col++).setCellValue(dto.getAssetId() != null ? dto.getAssetId() : 0);
                r.createCell(col++).setCellValue(dto.getAssetName() != null ? dto.getAssetName() : "");
                r.createCell(col++).setCellValue(dto.getReservedHours());
                r.createCell(col++).setCellValue(dto.getActualHours());
                r.createCell(col++).setCellValue(dto.getUtilizationRate() != null ? dto.getUtilizationRate() : 0);
                r.createCell(col++).setCellValue(dto.getPerformRate() != null ? dto.getPerformRate() : 0);
                r.createCell(col++)
                        .setCellValue(
                                dto.getTotalUsageCost() != null
                                        ? dto.getTotalUsageCost().doubleValue()
                                        : 0);
                r.createCell(col++)
                        .setCellValue(
                                dto.getActualUsageCost() != null
                                        ? dto.getActualUsageCost().doubleValue()
                                        : 0);
                r.createCell(col++)
                        .setCellValue(
                                dto.getUsageGapCost() != null
                                        ? dto.getUsageGapCost().doubleValue()
                                        : 0);
                r.createCell(col++).setCellValue(dto.getUtilizationGrade() != null ? dto.getUtilizationGrade() : "");
                r.createCell(col++).setCellValue(dto.getPerformGrade() != null ? dto.getPerformGrade() : "");

                for (int i = 0; i < headers.length; i++) {
                    r.getCell(i).setCellStyle(bodyStyle);
                }
            }

            // =======================
            // 4) 파일 출력
            // =======================
            String filename =
                    URLEncoder.encode("분기정산.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            wb.write(response.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException("엑셀 내보내기 실패", e);
        }
    }
}
