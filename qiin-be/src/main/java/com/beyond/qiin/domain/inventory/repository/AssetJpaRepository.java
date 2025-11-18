package com.beyond.qiin.domain.inventory.repository;

import com.beyond.qiin.domain.inventory.entity.Asset;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetJpaRepository extends JpaRepository<Asset, Long> {

    boolean existsByName(String name);

    Optional<Asset> findByName(String name);


}
