package com.beyond.qiin.domain.accounting.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class YearListResponseDto {
    private final List<Integer> years;
}
