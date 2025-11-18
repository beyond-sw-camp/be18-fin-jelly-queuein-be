package com.beyond.qiin.domain.inventory.dto.asset.request;

import com.beyond.qiin.domain.inventory.entity.Asset;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateAssetRequestDto {

    private String parentName;

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

    public Asset toEntity() {
        return Asset.builder()
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .image(image)
                .status(status)
                .type(type)
                .accessLevel(accessLevel)
                .approvalStatus(approvalStatus)
                .costPerHour(costPerHour)
                .periodCost(periodCost)
                .build();
    }
}
