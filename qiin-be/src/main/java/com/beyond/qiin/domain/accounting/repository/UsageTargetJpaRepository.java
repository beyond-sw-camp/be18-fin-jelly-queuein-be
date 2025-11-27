package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  UsageTargetJpaRepository extends JpaRepository <UsageTarget, Long> {
    Optional<UsageTarget> findByYear(int year);
}
