package com.beyond.qiin.domain.iam.support.department;

import com.beyond.qiin.domain.iam.entity.Department;
import com.beyond.qiin.domain.iam.exception.DepartmentException;
import com.beyond.qiin.domain.iam.repository.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentReader {

    private final DepartmentJpaRepository departmentJpaRepository;

    public Department getById(final Long departmentId) {
        return departmentJpaRepository.findById(departmentId).orElseThrow(DepartmentException::notFound);
    }

    public boolean existsByName(final String dptName) {
        return departmentJpaRepository.existsByDptName(dptName);
    }
}
