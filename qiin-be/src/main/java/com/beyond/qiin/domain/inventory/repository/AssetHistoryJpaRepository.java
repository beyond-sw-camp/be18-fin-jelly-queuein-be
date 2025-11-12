package com.beyond.qiin.domain.inventory.repository;

import com.beyond.qiin.domain.inventory.entity.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetHistoryJpaRepository extends JpaRepository<AssetHistory, Long> {}
