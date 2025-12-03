package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.UpdateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.exception.CategoryException;
import com.beyond.qiin.domain.inventory.repository.AssetJpaRepository;
import com.beyond.qiin.domain.inventory.repository.CategoryJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("카테고리 커맨드 서비스 단위 테스트")
class CategoryCommandServiceImplTest {

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @Mock
    private AssetJpaRepository assetJpaRepository;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private CategoryCommandServiceImpl service;

    // CREATE
    @Test
    @DisplayName("카테고리 생성 - 이름 중복이면 예외")
    void createCategory_duplicateName() {
        // given
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto("카테고리", "단위테스트");

        when(categoryJpaRepository.existsByName("카테고리")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> service.createCategory(dto))
                .isInstanceOf(CategoryException.class);

        verify(categoryJpaRepository, times(1)).existsByName("카테고리");
        verify(categoryJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("카테고리 생성 - 정상 생성")
    void createCategory_success() {
        // given
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto("카테고리", "단위테스트");

        when(categoryJpaRepository.existsByName("카테고리")).thenReturn(false);

        when(categoryJpaRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category c = invocation.getArgument(0);
                    ReflectionTestUtils.setField(c, "id", 1L);
                    return c;
                });

        // when
        CreateCategoryResponseDto result = service.createCategory(dto);

        // then
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("카테고리");

        verify(categoryJpaRepository, times(1)).save(any(Category.class));
    }

    // UPDATE
    @Test
    @DisplayName("카테고리 수정 - 이름 중복이면 예외")
    void updateCategory_duplicateName() {
        // given
        UpdateCategoryRequestDto dto = new UpdateCategoryRequestDto("새이름","단위테스트");

        when(categoryJpaRepository.existsByName("새이름")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> service.updateCategory(dto, 1L))
                .isInstanceOf(CategoryException.class);
    }

    @Test
    @DisplayName("카테고리 수정 - 존재하지 않으면 예외")
    void updateCategory_notFound() {
        // given
        UpdateCategoryRequestDto dto = new UpdateCategoryRequestDto("새이름", "단위테스트");

        when(categoryJpaRepository.existsByName("새이름")).thenReturn(false);
        when(categoryJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.updateCategory(dto, 1L))
                .isInstanceOf(CategoryException.class);
    }

    @Test
    @DisplayName("카테고리 수정 - 정상 흐름")
    void updateCategory_success() {
        // given
        UpdateCategoryRequestDto dto = new UpdateCategoryRequestDto("새이름", "단위테스트");

        Category category = Category.builder()
                                    .name("기존이름")
                                    .description("단위테스트")
                                    .build();
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryJpaRepository.existsByName("새이름")).thenReturn(false);
        when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));

        // when
        UpdateCategoryResponseDto result = service.updateCategory(dto, 1L);

        // then
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("새이름");
    }

    // DELETE
    @Test
    @DisplayName("카테고리 삭제 - 자원이 존재하면 예외")
    void softDeleteCategory_hasAssets() {
        // given
        when(userReader.findById(9L)).thenReturn(null);

        Category category = Category.builder()
                                    .name("카테고리")
                                    .description(null)
                                    .build();
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));
        when(assetJpaRepository.existsByCategoryId(1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> service.softDeleteCategory(1L, 9L))
                .isInstanceOf(CategoryException.class);
    }

    @Test
    @DisplayName("카테고리 삭제 - 정상 softDelete")
    void softDeleteCategory_success() {
        // given
        Category category = Category.builder()
                                    .name("기존이름")
                                    .description("단위테스트")
                                    .build();
        ReflectionTestUtils.setField(category, "id", 1L);

        when(userReader.findById(9L)).thenReturn(null);
        when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));
        when(assetJpaRepository.existsByCategoryId(1L)).thenReturn(false);

        // when
        service.softDeleteCategory(1L, 9L);

        // then
        assertThat(category.getDeletedBy()).isEqualTo(9L);
        assertThat(category.getDeletedAt()).isNotNull();

        verify(categoryJpaRepository).save(category);
    }

    // VALIDATE
    @Test
    @DisplayName("validateCategoryId - 존재하지 않으면 예외")
    void validateCategoryId_notFound() {
        when(categoryJpaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.validateCategoryId(1L))
                .isInstanceOf(CategoryException.class);
    }

    @Test
    @DisplayName("validateCategoryId - 정상")
    void validateCategoryId_success() {
        when(categoryJpaRepository.existsById(1L)).thenReturn(true);

        service.validateCategoryId(1L);
    }
}
