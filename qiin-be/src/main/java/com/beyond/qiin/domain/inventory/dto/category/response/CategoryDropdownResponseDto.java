package com.beyond.qiin.domain.inventory.dto.category.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDropdownResponseDto {

    private Long categoryId;

    private String name;

    //    public static CategoryDropdownResponseDto fromEntity(Category category) {
    //        return CategoryDropdownResponseDto.builder()
    //                                          .categoryId(category.getId())
    //                                          .name(category.getName())
    //                                          .build();
    //    }

}
