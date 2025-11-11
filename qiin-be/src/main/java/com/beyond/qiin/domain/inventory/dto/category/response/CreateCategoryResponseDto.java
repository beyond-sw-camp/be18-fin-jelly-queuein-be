package com.beyond.qiin.domain.inventory.dto.category.response;

import com.beyond.qiin.domain.inventory.entity.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryResponseDto {

    private Long categoryId;

    private String name;

    private String description;

    public static CreateCategoryResponseDto fromEntity(Category category) {
        return CreateCategoryResponseDto.builder()
                                        .categoryId(category.getId())
                                        .name(category.getName())
                                        .description(category.getDescription())
                                        .build();
    }
}
