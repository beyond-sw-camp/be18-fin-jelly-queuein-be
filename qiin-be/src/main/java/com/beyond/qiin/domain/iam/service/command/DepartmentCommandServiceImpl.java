package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.department.request.CreateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.request.UpdateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentDetailResponseDto;
import com.beyond.qiin.domain.iam.entity.Department;
import com.beyond.qiin.domain.iam.repository.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentCommandServiceImpl implements DepartmentCommandService {

  private final DepartmentJpaRepository departmentJpaRepository;

  // 부서 생성
  @Override
  @Transactional
  public DepartmentDetailResponseDto createDepartment(final CreateDepartmentRequestDto request) {

    final Department savedDepartment = departmentJpaRepository.save(Department.create(request));

    return DepartmentDetailResponseDto.fromEntity(savedDepartment);
  }

  // 부서명 수정
  @Override
  @Transactional
  public void updateDepartment(final UpdateDepartmentRequestDto request) {

  }

  // 부서 관계 수정
  @Override
  @Transactional
  public void updateDepartmentRelation(final UpdateDepartmentRequestDto request) {

  }

  // 부서 삭제
  @Override
  @Transactional
  public void deleteDepartment(final Long departmentId) {

  }

  ;
}
