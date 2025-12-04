package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.category.response.DropdownCategoryResponseDto;
import com.beyond.qiin.domain.inventory.dto.category.response.ManageCategoryResponseDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryQueryRepository {

    List<DropdownCategoryResponseDto> findAllForDropdown();

    Page<ManageCategoryResponseDto> findAllForManage(Pageable pageable);

    Optional<String> findNameById(Long categoryId);
}
