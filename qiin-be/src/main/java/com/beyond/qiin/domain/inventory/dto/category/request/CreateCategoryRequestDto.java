package com.beyond.qiin.domain.inventory.dto.category.request;

import com.beyond.qiin.domain.inventory.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryRequestDto {

    @NotBlank
    private String name;

    private String description;

    public Category createCategory() {
        return Category.builder()
                       .name(name)
                       .description(description)
                       .build();
    }
}
