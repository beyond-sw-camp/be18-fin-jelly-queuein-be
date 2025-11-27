package com.beyond.qiin.domain.inventory.dto.asset.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class CreateAssetRequestDto {

    private String parentName;

    @NotNull
    private Long categoryId;

    @NotBlank
    private String name;

    private String description;

    private String image;

    @NotBlank
    private String status;

    @NotBlank
    private String type;

    @NotNull
    private Integer accessLevel;

    @NotNull
    private Boolean approvalStatus;

    @NotNull
    private BigDecimal costPerHour;

    @NotNull
    private BigDecimal periodCost;

}


