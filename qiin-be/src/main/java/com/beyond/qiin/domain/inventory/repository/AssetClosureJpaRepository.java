package com.beyond.qiin.domain.inventory.repository;

import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetClosureJpaRepository extends JpaRepository<AssetClosure,Long> {

}
