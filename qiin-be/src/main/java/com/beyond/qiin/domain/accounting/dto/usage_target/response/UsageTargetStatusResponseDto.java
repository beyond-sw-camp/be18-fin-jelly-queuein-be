package com.beyond.qiin.domain.accounting.dto.usage_target.response;

import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsageTargetStatusResponseDto {
    private boolean exists;
    private Integer year;
    private BigDecimal targetRate;

    public static UsageTargetStatusResponseDto exists(UsageTarget entity) {
        return UsageTargetStatusResponseDto.builder()
                .exists(true)
                .year(entity.getYear())
                .targetRate(entity.getTargetRate())
                .build();
    }

    public static UsageTargetStatusResponseDto notExists(Integer year) {
        return UsageTargetStatusResponseDto.builder().exists(false).year(year).build();
    }
}
