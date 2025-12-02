package com.beyond.qiin.domain.accounting.dto.usage_target.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsageTargetResponseDto {
    private Long id;
    private Integer year;
    private BigDecimal targetRate;
    private Long createdBy;
    private String createdAt;
}
