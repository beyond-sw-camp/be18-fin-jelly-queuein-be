package com.beyond.qiin.domain.inventory.dto.asset.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateAssetRequestDto {

    private Long categoryId;

    private String name;

    private String description;

    private String image;

    private Integer status;

    private Integer type;

    private Integer accessLevel;

    private Boolean approvalStatus;

    private BigDecimal costPerHour;

    private BigDecimal periodCost;
}
