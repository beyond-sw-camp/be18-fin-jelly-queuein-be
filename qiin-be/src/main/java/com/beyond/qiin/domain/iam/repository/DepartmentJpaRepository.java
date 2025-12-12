package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.Department;
import com.beyond.qiin.domain.iam.repository.querydsl.DepartmentQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<Department, Long>, DepartmentQueryRepository {

    Department findDepartmentById(final Long id);

    boolean existsByDptName(final String dptName);
}
