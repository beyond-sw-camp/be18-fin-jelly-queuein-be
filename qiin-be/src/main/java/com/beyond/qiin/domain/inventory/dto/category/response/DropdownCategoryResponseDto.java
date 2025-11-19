package com.beyond.qiin.domain.inventory.dto.category.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DropdownCategoryResponseDto {

    private final Long categoryId;

    private final String name;
}
