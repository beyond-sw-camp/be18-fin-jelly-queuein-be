package com.beyond.qiin.domain.iam.service.query;

import com.beyond.qiin.domain.iam.dto.department.response.DepartmentListResponseDto;
import com.beyond.qiin.domain.iam.repository.querydsl.DepartmentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentQueryServiceImpl implements DepartmentQueryService {

    private final DepartmentQueryRepository departmentQueryRepository;

    public DepartmentListResponseDto getDepartments() {
        return DepartmentListResponseDto.from(departmentQueryRepository.findAllWithUserCount());
    }
}
