package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageHistoryJpaRepository extends JpaRepository<UsageHistory, Long> {}
