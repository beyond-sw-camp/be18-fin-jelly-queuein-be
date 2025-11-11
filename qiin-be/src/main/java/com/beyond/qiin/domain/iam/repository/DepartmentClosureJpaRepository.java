package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.DepartmentClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentClosureJpaRepository extends JpaRepository<DepartmentClosure, Integer> {

}
