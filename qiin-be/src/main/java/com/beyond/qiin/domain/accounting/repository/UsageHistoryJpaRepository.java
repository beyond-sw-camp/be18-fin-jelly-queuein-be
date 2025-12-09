package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsageHistoryJpaRepository extends JpaRepository<UsageHistory, Long> {
    @Query("SELECT DISTINCT YEAR(u.startAt) FROM UsageHistory u ORDER BY YEAR(u.startAt) ASC")
    List<Integer> findExistingYears();
}
