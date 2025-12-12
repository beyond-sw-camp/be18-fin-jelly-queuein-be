package com.beyond.qiin.domain.iam.service.command;

import com.beyond.qiin.domain.iam.dto.department.request.CreateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.request.UpdateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentResponseDto;
import com.beyond.qiin.domain.iam.entity.Department;
import com.beyond.qiin.domain.iam.exception.DepartmentException;
import com.beyond.qiin.domain.iam.support.department.DepartmentReader;
import com.beyond.qiin.domain.iam.support.department.DepartmentWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentCommandServiceImpl implements DepartmentCommandService {

    private final DepartmentReader departmentReader;
    private final DepartmentWriter departmentWriter;

    // 부서 생성
    @Override
    @Transactional
    public DepartmentResponseDto createDepartment(final CreateDepartmentRequestDto request) {

        if (departmentReader.existsByName(request.getDptName())) {
            throw DepartmentException.duplicateName();
        }

        Department department = Department.create(request);
        Department saved = departmentWriter.save(department);

        return DepartmentResponseDto.of(saved, 0L);
    }

    // 부서명 수정
    @Override
    @Transactional
    public void updateDepartment(final UpdateDepartmentRequestDto request) {}

    // 부서 관계 수정
    @Override
    @Transactional
    public void updateDepartmentRelation(final UpdateDepartmentRequestDto request) {}

    // 부서 삭제
    @Override
    @Transactional
    public void deleteDepartment(final Long departmentId) {

        Department department = departmentReader.getById(departmentId);

        // TODO: 하위 부서 존재 여부 검사 (나중에)
        department.delete(departmentId);
    }
}
