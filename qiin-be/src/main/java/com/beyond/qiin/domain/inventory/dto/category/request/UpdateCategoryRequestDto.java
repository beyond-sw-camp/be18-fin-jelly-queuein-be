package com.beyond.qiin.domain.inventory.dto.category.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateCategoryRequestDto {

    @NotBlank
    private String name;

    private String description;
}
