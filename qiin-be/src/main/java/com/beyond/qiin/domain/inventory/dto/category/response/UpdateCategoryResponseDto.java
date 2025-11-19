package com.beyond.qiin.domain.inventory.dto.category.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateCategoryResponseDto {

    private final Long categoryId;

    private final String name;

    private final String description;
}
