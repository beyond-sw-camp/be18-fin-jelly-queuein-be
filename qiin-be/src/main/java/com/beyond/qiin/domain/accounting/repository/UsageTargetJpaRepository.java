package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsageTargetJpaRepository extends JpaRepository<UsageTarget, Long> {
    Optional<UsageTarget> findByYear(int year);

    boolean existsByYear(Integer year);

    @Query("select t.year from UsageTarget t order by t.year asc")
    List<Integer> findAllYears();
}
