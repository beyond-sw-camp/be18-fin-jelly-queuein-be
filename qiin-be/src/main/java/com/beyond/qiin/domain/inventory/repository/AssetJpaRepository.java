package com.beyond.qiin.domain.inventory.repository;

import com.beyond.qiin.domain.inventory.entity.Asset;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetJpaRepository extends JpaRepository<Asset, Long> {

    boolean existsByName(String name);

    Optional<Asset> findByName(String name);

    // 카테고리를 가지는 자원이 있는지 없는지 여부
    boolean existsByCategoryId(Long categoryId);

    boolean existsByNameAndIdNot(String name, Long id);
}
