package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //실행해보고 없도 되면 생략
public class UsageHistorySearchRequest {

    private Instant startDate; // 조회 시작 날짜
    private Instant endDate; // 조회 종료 날짜

    private Long categoryId; // 카테고리
    private Long subCategoryId; // 상세 카테고리
    private String keyword; // 검색어 (자원명 등)
}
