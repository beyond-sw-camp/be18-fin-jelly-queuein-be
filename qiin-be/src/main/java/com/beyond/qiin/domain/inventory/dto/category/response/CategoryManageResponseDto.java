package com.beyond.qiin.domain.inventory.dto.category.response;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryManageResponseDto {

    private Long categoryId;

    private String name;

    private String description;

    private Long assetCount;

    private Instant createdAt;

    private Long createdBy;

    //    public static CategoryManageResponseDto fromEntity(Category category) {
    //        return CategoryManageResponseDto.builder()
    //                                        .categoryId(category.getId())
    //                                        .name(category.getName())
    //                                        .description(category.getDescription())
    //                                        .build();
    //
    //    }
}
