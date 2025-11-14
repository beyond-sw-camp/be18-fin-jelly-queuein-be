package com.beyond.qiin.domain.inventory.controller.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import com.beyond.qiin.domain.inventory.service.query.CategoryQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets/categories")
@RequiredArgsConstructor
public class CategoryQueryController {

    private final CategoryQueryService categoryQueryService;

    @GetMapping("/dropdown")
    public ResponseEntity<List<DropdownCategoryResponseDto>> getDropdownCategory() {

        List<DropdownCategoryResponseDto> categories = categoryQueryService.getDropdownList();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/manage")
    public ResponseEntity<PageResponseDto<ManageCategoryResponseDto>> getManageCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "010") int size
    ) {

        PageResponseDto<ManageCategoryResponseDto> categories = categoryQueryService.getManageList(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }
}
