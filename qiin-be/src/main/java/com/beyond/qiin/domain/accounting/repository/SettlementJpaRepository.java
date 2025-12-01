package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementJpaRepository extends JpaRepository<Settlement, Long> {}
