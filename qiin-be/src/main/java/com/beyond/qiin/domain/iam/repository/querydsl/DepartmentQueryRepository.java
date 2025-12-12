package com.beyond.qiin.domain.iam.repository.querydsl;

import com.beyond.qiin.domain.iam.dto.department.response.DepartmentResponseDto;
import java.util.List;

public interface DepartmentQueryRepository {

    List<DepartmentResponseDto> findAllWithUserCount();
}
