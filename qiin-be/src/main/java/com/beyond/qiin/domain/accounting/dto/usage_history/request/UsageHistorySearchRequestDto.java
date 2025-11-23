package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 실행해보고 없도 되면 생략
public class UsageHistorySearchRequestDto {

    private Integer page = 0; // 기본값
    private Integer size = 20; // 기본값

    private Instant startDate; // 조회 시작 날짜
    private Instant endDate; // 조회 종료 날짜

    private String keyword; // 검색어 (자원명 등)
}
