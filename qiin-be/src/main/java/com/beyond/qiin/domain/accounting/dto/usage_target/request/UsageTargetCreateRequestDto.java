package com.beyond.qiin.domain.accounting.dto.usage_target.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UsageTargetCreateRequestDto {

    @NotNull(message = "목표 사용률은 필수입니다.")
    @DecimalMin(value = "0.0", message = "목표 사용률은 0 이상이어야 합니다.")
    private Double targetRate; // BigDecimal로 변환 예정 (Service에서)
}
