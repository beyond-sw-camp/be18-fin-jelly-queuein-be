package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    @Transactional
    public CreateCategoryResponseDto createCategory(CreateCategoryRequestDto requestDto) {

        //권한 검증


        Category category = requestDto.createCategory();

        categoryJpaRepository.save(category);


        return CreateCategoryResponseDto.fromEntity(category);
    }

    @Override
    public void updateCategory(UpdateCategoryRequestDto requestDto) {

    }

    @Override
    public void deleteCategory(String categoryId) {

    }
}
