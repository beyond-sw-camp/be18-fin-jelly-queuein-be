package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.UpdateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.exception.CategoryException;
import com.beyond.qiin.domain.inventory.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryJpaRepository categoryJpaRepository;

    private final UserReader userReader;

    // 생성
    @Override
    @Transactional
    public CreateCategoryResponseDto createCategory(final CreateCategoryRequestDto requestDto) {

        // 나중에 권한 검증 추가

        // 이름 중복이면 예외 처리
        if (categoryJpaRepository.existsByName(requestDto.getName())) {
            throw CategoryException.duplicateName();
        }

        Category category = requestDto.toEntity();

        categoryJpaRepository.save(category);

        return CreateCategoryResponseDto.fromEntity(category);
    }

    // 수정
    @Override
    @Transactional
    public UpdateCategoryResponseDto updateCategory(final UpdateCategoryRequestDto requestDto, final Long categoryId) {

        // 나중에 권한 검증 추가

        // 이름 중복이면 예외 처리
        if (categoryJpaRepository.existsByName(requestDto.getName())) {
            throw CategoryException.duplicateName();
        }

        Category category = categoryJpaRepository.findById(categoryId).orElseThrow(CategoryException::notFound);

        category.apply(requestDto);

        return UpdateCategoryResponseDto.fromEntity(category);
    }

    // 삭제
    @Override
    @Transactional
    public void softDeleteCategory(final Long categoryId, final Long userId) {

        // 나중에 권한 검증 추가

        // 삭제하는 유저 찾기
        userReader.findById(userId);

        // 삭제할 카테고리 찾기
        Category category = categoryJpaRepository.findById(categoryId).orElseThrow(CategoryException::notFound);

        // softDelete로 삭제 처리
        category.softDelete(userId);
        categoryJpaRepository.save(category);
    }

    // 카테고리 id 존재 검증 메소드
    @Override
    @Transactional
    public void validateCategoryId(final Long categoryId) {
        if (!categoryJpaRepository.existsById(categoryId)) {
            throw CategoryException.notFound();
        }
    }
}
