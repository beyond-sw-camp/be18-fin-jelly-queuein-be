package com.beyond.qiin.domain.inventory.dto.category.response;

import com.beyond.qiin.domain.inventory.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateCategoryResponseDto {

    private final Long categoryId;

    private final String name;

    private final String description;

    public static CreateCategoryResponseDto fromEntity(Category category) {
        return CreateCategoryResponseDto.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
