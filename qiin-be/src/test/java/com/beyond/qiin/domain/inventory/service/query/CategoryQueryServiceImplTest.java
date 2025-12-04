package com.beyond.qiin.domain.inventory.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import com.beyond.qiin.domain.inventory.repository.querydsl.CategoryQueryAdapter;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("카테고리 쿼리 서비스 단위 테스트")
class CategoryQueryServiceImplTest {

    @Mock
    private CategoryQueryAdapter categoryQueryAdapter;

    @InjectMocks
    private CategoryQueryServiceImpl service;

    // DROPDOWN
    @Test
    @DisplayName("드롭다운용 카테고리 리스트 조회 - 정상 흐름")
    void getDropdownList_success() {
        // given
        List<DropdownCategoryResponseDto> mockList =
                List.of(new DropdownCategoryResponseDto(1L, "A"), new DropdownCategoryResponseDto(2L, "B"));

        when(categoryQueryAdapter.findAllForDropdown()).thenReturn(mockList);

        // when
        List<DropdownCategoryResponseDto> result = service.getDropdownList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
        assertThat(result.get(1).getName()).isEqualTo("B");

        verify(categoryQueryAdapter, times(1)).findAllForDropdown();
    }

    // MANAGE LIST
    @Test
    @DisplayName("카테고리 관리 리스트 조회 - 정상 흐름")
    void getManageList_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        ManageCategoryResponseDto dto1 = ManageCategoryResponseDto.builder()
                .categoryId(1L)
                .name("A")
                .description("설명")
                .assetCount(3L)
                .createdAt(Instant.now())
                .createdBy(1L)
                .build();

        ManageCategoryResponseDto dto2 = ManageCategoryResponseDto.builder()
                .categoryId(2L)
                .name("B")
                .description("설명2")
                .assetCount(5L)
                .createdAt(Instant.now())
                .createdBy(1L)
                .build();

        Page<ManageCategoryResponseDto> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

        when(categoryQueryAdapter.findAllForManage(any(Pageable.class))).thenReturn(page);

        // when
        PageResponseDto<ManageCategoryResponseDto> result = service.getManageList(0, 10);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("A");
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(categoryQueryAdapter, times(1)).findAllForManage(any(Pageable.class));
    }
}
