package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.department.request.CreateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentDetailResponseDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentListResponseDto;

public interface DepartmentCommandService {

  // 부서 생성
  DepartmentDetailResponseDto createDepartment(final CreateDepartmentRequestDto request);

}
