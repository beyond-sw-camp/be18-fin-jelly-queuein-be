package com.beyond.qiin.domain.iam.support.department;

import com.beyond.qiin.domain.iam.entity.Department;
import com.beyond.qiin.domain.iam.repository.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentWriter {

    private final DepartmentJpaRepository departmentJpaRepository;

    public Department save(final Department department) {
        return departmentJpaRepository.save(department);
    }
}
