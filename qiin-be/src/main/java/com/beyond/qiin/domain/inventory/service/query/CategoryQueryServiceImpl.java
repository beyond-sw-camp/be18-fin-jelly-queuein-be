package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.domain.inventory.dto.category.response.CategoryDropdownResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CategoryManageResponseDto;
import com.beyond.qiin.domain.inventory.repository.querydsl.CategoryQueryAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryQueryAdapter categoryQueryAdapter;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDropdownResponseDto> getDropdownList() {
        return categoryQueryAdapter.findAllForDropdown();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryManageResponseDto> getManageList() {
        return categoryQueryAdapter.findAllForManage();
    }
}
