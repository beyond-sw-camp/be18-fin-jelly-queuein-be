package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsageHistorySearchRequest {

    private Instant startDate;      // 조회 시작 날짜
    private Instant endDate;        // 조회 종료 날짜

    private Long categoryId;          // 카테고리
    private Long subCategoryId;       // 상세 카테고리
    private String keyword;           // 검색어 (자원명 등)
}
