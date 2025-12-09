package com.beyond.qiin.domain.inventory.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.search_condition.AssetSearchCondition;
import com.beyond.qiin.domain.inventory.dto.asset.response.AssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.OneDepthAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.RootAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawAssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawDescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetClosureQueryRepository;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetQueryRepository;
import com.beyond.qiin.infra.redis.inventory.AssetDetailRedisAdapter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AssetQueryServiceImplTest {

    @Mock
    private AssetQueryRepository assetQueryRepository;

    @Mock
    private AssetClosureQueryRepository assetClosureQueryRepository;

    @Mock
    private AssetDetailRedisAdapter assetDetailRedisAdapter;

    @InjectMocks
    private AssetQueryServiceImpl service;

    // -------------------------------------------------------
    // ROOT 조회
    // -------------------------------------------------------
    @Test
    @DisplayName("루트 자원 조회 - 정상 흐름")
    void getRootAssetIds_success() {

        when(assetQueryRepository.findRootAssetIds()).thenReturn(List.of(1L, 2L));

        Asset a1 = AssetFactory.asset(1L, "A");
        Asset a2 = AssetFactory.asset(2L, "B");

        when(assetQueryRepository.findByIds(List.of(1L, 2L))).thenReturn(List.of(a1, a2));

        List<RootAssetResponseDto> result = service.getRootAssetIds();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAssetId()).isEqualTo(1L);
        assertThat(result.get(1).getName()).isEqualTo("B");
    }

    // -------------------------------------------------------
    // ONE DEPTH 조회
    // -------------------------------------------------------
    @Test
    @DisplayName("1 Depth 자원 조회 - 정상 흐름")
    void getOneDepthAssetList_success() {

        when(assetQueryRepository.findChildrenIds(10L)).thenReturn(List.of(20L, 30L));

        Asset c1 = AssetFactory.asset(20L, "child1");
        Asset c2 = AssetFactory.asset(30L, "child2");

        when(assetQueryRepository.findByIds(List.of(20L, 30L))).thenReturn(List.of(c1, c2));

        List<OneDepthAssetResponseDto> result = service.getOneDepthAssetList(10L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("child1");
    }

    // -------------------------------------------------------
    // DESCENDANT PAGE 조회
    // -------------------------------------------------------
    @Test
    @DisplayName("Descendant 자원 목록 조회 - PageResponseDto 정상 변환")
    void getDescendantAssetList_success() {

        Pageable pageable = PageRequest.of(0, 10);
        AssetSearchCondition condition = new AssetSearchCondition();

        RawDescendantAssetResponseDto r1 = RawDescendantAssetResponseDto.builder()
                .assetId(1L)
                .name("AA")
                .categoryName("카테고리1")
                .status(0)
                .type(0)
                .needApproval(false)
                .reservable(true)
                .version(0L)
                .build();

        RawDescendantAssetResponseDto r2 = RawDescendantAssetResponseDto.builder()
                .assetId(2L)
                .name("BB")
                .categoryName("카테고리1")
                .status(0)
                .type(0)
                .needApproval(false)
                .reservable(true)
                .version(0L)
                .build();

        Page<RawDescendantAssetResponseDto> rawPage = new PageImpl<>(List.of(r1, r2), pageable, 2);

        when(assetQueryRepository.searchDescendants(any(AssetSearchCondition.class), any(Pageable.class)))
                .thenReturn(rawPage);

        PageResponseDto<DescendantAssetResponseDto> result = service.getDescendantAssetList(condition, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    // -------------------------------------------------------
    // 트리 조회
    // -------------------------------------------------------
    @Test
    @DisplayName("전체 트리 조회 - 정상 흐름")
    void getFullAssetTree_success() {

        // 전체 자원 Mock
        Asset a1 = AssetFactory.asset(1L, "Root");
        Asset a2 = AssetFactory.asset(2L, "Child1");
        Asset a3 = AssetFactory.asset(3L, "Child2");

        when(assetQueryRepository.findAll()).thenReturn(List.of(a1, a2, a3));

        // depth=1 관계 Mock (root -> children)
        when(assetClosureQueryRepository.findDepthOneRelations())
                .thenReturn(List.of(AssetFactory.closure(1L, 2L, 1), AssetFactory.closure(1L, 3L, 1)));

        // 실행
        List<TreeAssetResponseDto> result = service.getFullAssetTree();

        // root는 1개: ID=1
        assertThat(result).hasSize(1);

        TreeAssetResponseDto rootTree = result.get(0);

        assertThat(rootTree.getAssetId()).isEqualTo(1L);
        assertThat(rootTree.getChildren()).hasSize(2);
        assertThat(rootTree.getChildren().get(0).getAssetId()).isEqualTo(2L);
        assertThat(rootTree.getChildren().get(1).getAssetId()).isEqualTo(3L);
    }

    // -------------------------------------------------------
    // 상세 조회
    // -------------------------------------------------------
    @Test
    @DisplayName("자원 상세 조회 - 정상")
    void getAssetDetail_success() {

        RawAssetDetailResponseDto raw = RawAssetDetailResponseDto.builder()
                .assetId(10L)
                .name("A")
                .description("desc")
                .image(null)
                .categoryId(1L)
                .categoryName("카테고리A")
                .status(1)
                .type(1)
                .accessLevel(1)
                .approvalStatus(true)
                .costPerHour(BigDecimal.valueOf(5000))
                .periodCost(BigDecimal.TEN)
                .createdAt(Instant.now())
                .createdBy(100L)
                .build();

        when(assetQueryRepository.findByAssetId(10L)).thenReturn(Optional.of(raw));
        when(assetQueryRepository.findParentName(10L)).thenReturn("Parent");

        AssetDetailResponseDto result = service.getAssetDetail(10L);

        assertThat(result.getAssetId()).isEqualTo(10L);
        assertThat(result.getParentName()).isEqualTo("Parent");
        assertThat(result.getCategoryName()).isEqualTo("카테고리A");
    }

    // -------------------------------------------------------
    // Factory Helpers
    // -------------------------------------------------------
    static class AssetFactory {

        static Asset asset(Long id, String name) {
            Asset a = mock(Asset.class);
            when(a.getId()).thenReturn(id);
            when(a.getName()).thenReturn(name);
            return a;
        }

        static AssetClosure closure(Long ancestor, Long descendant, int depth) {
            return AssetClosure.of(ancestor, descendant, depth);
        }
    }
}
