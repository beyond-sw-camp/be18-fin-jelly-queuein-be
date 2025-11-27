package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageTargetJpaRepository extends JpaRepository<UsageTarget, Long> {
    Optional<UsageTarget> findByYear(int year);
}
