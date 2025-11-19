package com.beyond.qiin.domain.inventory.dto.asset.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateAssetRequestDto {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String name;

    private String description;

    private String image;

    @NotNull
    private Integer status;

    @NotNull
    private Integer type;

    @NotNull
    private Integer accessLevel;

    @NotNull
    private Boolean approvalStatus;

    @NotNull
    private BigDecimal costPerHour;

    @NotNull
    private BigDecimal periodCost;
}
