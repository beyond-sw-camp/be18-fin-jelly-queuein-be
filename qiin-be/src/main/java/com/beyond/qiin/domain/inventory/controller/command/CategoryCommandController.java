package com.beyond.qiin.domain.inventory.controller.command;

import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.UpdateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.service.command.CategoryCommandService;
import com.beyond.qiin.security.resolver.AccessToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets/categories")
@RequiredArgsConstructor
public class CategoryCommandController {

    private final CategoryCommandService categoryCommandService;

    // 카테고리 생성
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<CreateCategoryResponseDto> createCategory(
            @Valid @RequestBody CreateCategoryRequestDto requestDto) {

        CreateCategoryResponseDto createCategoryResponseDto = categoryCommandService.createCategory(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createCategoryResponseDto);
    }

    // 카테고리 수정
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER')")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<UpdateCategoryResponseDto> updateCategory(
            @PathVariable Long categoryId, @Valid @RequestBody UpdateCategoryRequestDto requestDto) {

        UpdateCategoryResponseDto updateCategoryResponseDto =
                categoryCommandService.updateCategory(requestDto, categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(updateCategoryResponseDto);
    }

    // 카테고리 삭제
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId, @AccessToken final String accessToken) {

        //        categoryCommandService.deleteCategory(categoryId, user.getUserId());

        return ResponseEntity.noContent().build();
    }
}
