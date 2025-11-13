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

    // 생성
    @Override
    @Transactional
    public CreateCategoryResponseDto createCategory(final CreateCategoryRequestDto requestDto) {

        // 나중에 권한 검증 추가

        // 나중에 이름 중복이면 예외처리 추가

        Category category = Category.createFromDto(requestDto);

        categoryJpaRepository.save(category);

        return CreateCategoryResponseDto.fromEntity(category);
    }

    // 수정
    @Override
    @Transactional
    public void updateCategory(final UpdateCategoryRequestDto requestDto, final Long categoryId) {

        // 나중에 권한 검증 추가

        // 나중에 이름 중복이면 예외처리 추가

        Category category = categoryJpaRepository.findById(categoryId).orElse(null);

        category.updateFromDto(requestDto);
    }

    // 삭제
    @Override
    @Transactional
    public void deleteCategory(final Long categoryId, final Long userId) {

        // 나중에 권한 검증 추가

        Category category = categoryJpaRepository.findById(categoryId).orElse(null);

        category.delete(userId);
    }
}
