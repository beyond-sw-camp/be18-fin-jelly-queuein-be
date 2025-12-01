package com.beyond.qiin.domain.accounting.repository;

import com.beyond.qiin.domain.accounting.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryJpaRepository extends JpaRepository<UserHistory, Long> {}
