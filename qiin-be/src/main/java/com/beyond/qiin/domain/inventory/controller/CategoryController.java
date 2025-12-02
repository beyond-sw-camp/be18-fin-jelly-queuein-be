package com.beyond.qiin.domain.inventory.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.UpdateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.service.command.CategoryCommandService;
import com.beyond.qiin.domain.inventory.service.query.CategoryQueryService;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.resolver.AccessToken;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCommandService categoryCommandService;

    private final CategoryQueryService categoryQueryService;

    private final JwtTokenProvider jwtTokenProvider;

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

        final Long userId = jwtTokenProvider.getUserId(accessToken);

        categoryCommandService.softDeleteCategory(categoryId, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    // 카테고리 드롭다운 메뉴 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @GetMapping("/dropdown-menu")
    public ResponseEntity<List<DropdownCategoryResponseDto>> getDropdownCategory() {

        List<DropdownCategoryResponseDto> categories = categoryQueryService.getDropdownList();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    // 카테고리 관리자용 목록 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @GetMapping("/management")
    public ResponseEntity<PageResponseDto<ManageCategoryResponseDto>> getManageCategory(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        PageResponseDto<ManageCategoryResponseDto> categories = categoryQueryService.getManageList(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }
}
