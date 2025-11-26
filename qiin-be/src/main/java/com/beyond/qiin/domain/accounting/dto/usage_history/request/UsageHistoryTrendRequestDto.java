package com.beyond.qiin.domain.accounting.dto.usage_history.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UsageHistoryTrendRequestDto {

    private Integer baseYear; // null이면 서버에서 자동으로 올해-1 계산
    private Integer compareYear; // null이면 올해
    private Long assetId; // 자원 ID 검색
    private String assetName; // 자원명 검색 (like 검색)
}
