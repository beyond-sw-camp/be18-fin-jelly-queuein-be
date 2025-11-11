package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.department.request.CreateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.request.UpdateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentDetailResponseDto;

public interface DepartmentCommandService {

    // 부서 생성
    DepartmentDetailResponseDto createDepartment(final CreateDepartmentRequestDto request);

    // 부서 수정
    void updateDepartment(final UpdateDepartmentRequestDto request);

    // 부서 관계 수정
    void updateDepartmentRelation(final UpdateDepartmentRequestDto request);

    // 부서 삭제
    void deleteDepartment(final Long departmentId);
}
