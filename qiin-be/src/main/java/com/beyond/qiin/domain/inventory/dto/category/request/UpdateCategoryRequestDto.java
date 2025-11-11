package com.beyond.qiin.domain.inventory.dto.category.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCategoryRequestDto {

    private Long categoryId;

    @NotBlank
    private String name;

    private String description;
}
