package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import com.beyond.qiin.domain.inventory.repository.querydsl.CategoryQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryQueryRepository categoryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DropdownCategoryResponseDto> getDropdownList() {
        return categoryQueryRepository.findAllForDropdown();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<ManageCategoryResponseDto> getManageList(final int page, final int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ManageCategoryResponseDto> categoryManageResponseDtoPage = categoryQueryRepository.findAllForManage(pageable);

        return PageResponseDto.from(categoryManageResponseDtoPage);
    }

    //    @Override
    //    @Transactional(readOnly = true)
    //    public Category getCategoryById(final Long categoryId) {
    //
    //    }
}
