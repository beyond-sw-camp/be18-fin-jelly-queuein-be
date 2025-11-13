package com.beyond.qiin.domain.inventory.controller.query;

import com.beyond.qiin.domain.inventory.dto.category.response.CategoryDropdownResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CategoryManageResponseDto;
import com.beyond.qiin.domain.inventory.service.query.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets/categories")
@RequiredArgsConstructor
public class CategoryQueryController {

    private final CategoryQueryService categoryQueryService;

    @GetMapping("/dropdown")
    public ResponseEntity<List<CategoryDropdownResponseDto>> getDropdownCategory() {

        List<CategoryDropdownResponseDto> categories = categoryQueryService.getDropdownList();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/manage")
    public ResponseEntity<List<CategoryManageResponseDto>> getManageCategory() {

        List<CategoryManageResponseDto> categories = categoryQueryService.getManageList();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

}
