package com.beyond.qiin.domain.accounting.dto.common.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YearListResponseDto {
    private final List<Integer> years;
}
