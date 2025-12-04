package com.beyond.qiin.domain.inventory.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import com.beyond.qiin.domain.inventory.exception.AssetException;
import com.beyond.qiin.domain.inventory.repository.AssetClosureJpaRepository;
import com.beyond.qiin.domain.inventory.repository.AssetJpaRepository;
import com.beyond.qiin.domain.inventory.repository.CategoryJpaRepository;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetClosureQueryAdapter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AssetCommandServiceImplTest {

    @Mock
    private AssetJpaRepository assetJpaRepository;

    @Mock
    private AssetClosureJpaRepository assetClosureJpaRepository;

    @Mock
    private AssetClosureQueryAdapter assetClosureQueryAdapter;

    @Mock
    private CategoryCommandService categoryCommandService;

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private AssetCommandServiceImpl service;

    // -------------------------------------------------------
    // CREATE ASSET
    // -------------------------------------------------------
    @Test
    @DisplayName("자원 생성 - 이름 중복이면 예외")
    void createAsset_duplicateName() {

        CreateAssetRequestDto dto = new CreateAssetRequestDto(
                "", // parentName
                1L, // categoryId
                "프린터", // name
                "설명", // description
                null, // image
                "AVAILABLE", // status
                "STATIC", // type
                1, // accessLevel
                true, // approvalStatus
                BigDecimal.TEN, // costPerHour
                BigDecimal.ONE // periodCost
                );

        when(assetJpaRepository.existsByName("프린터")).thenReturn(true);

        assertThatThrownBy(() -> service.createAsset(dto)).isInstanceOf(AssetException.class);
    }

    @Test
    @DisplayName("자원 생성 - 루트 성공")
    void createAsset_root_success() {

        CreateAssetRequestDto dto = new CreateAssetRequestDto(
                "", 1L, "프린터", "설명", null, "AVAILABLE", "STATIC", 1, true, BigDecimal.TEN, BigDecimal.ONE);

        Category category = Category.builder().name("카테고리").description("desc").build();
        ReflectionTestUtils.setField(category, "id", 1L);

        when(assetJpaRepository.existsByName("프린터")).thenReturn(false);
        when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));

        when(assetJpaRepository.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset a = invocation.getArgument(0);
            ReflectionTestUtils.setField(a, "id", 100L);
            return a;
        });

        CreateAssetResponseDto result = service.createAsset(dto);

        assertThat(result.getAssetId()).isEqualTo(100L);
        assertThat(result.getParentId()).isNull();

        verify(assetClosureJpaRepository, times(1))
                .save(argThat(c -> c.getAssetClosureId().getAncestorId().equals(100L)
                        && c.getAssetClosureId().getDescendantId().equals(100L)
                        && c.getDepth() == 0));
    }

    @Test
    @DisplayName("자원 생성 - 부모 있을 때 클로저 생성 정상")
    void createAsset_withParent_success() {

        CreateAssetRequestDto dto = new CreateAssetRequestDto(
                "프린터", 1L, "노트북", "설명", null, "AVAILABLE", "STATIC", 1, true, BigDecimal.TEN, BigDecimal.ONE);

        Category category = Category.builder().name("카테고리").description("desc").build();
        ReflectionTestUtils.setField(category, "id", 1L);

        // 이름 중복 아님
        when(assetJpaRepository.existsByName("노트북")).thenReturn(false);
        when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));

        // 자식 asset 저장
        when(assetJpaRepository.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset a = invocation.getArgument(0);
            ReflectionTestUtils.setField(a, "id", 200L);
            return a;
        });

        // parent asset
        Asset parent = mock(Asset.class);
        when(parent.getId()).thenReturn(50L);
        when(assetJpaRepository.findByName("프린터")).thenReturn(Optional.of(parent));

        // parent 조상들
        AssetClosure rootClosure = AssetClosure.of(1L, 50L, 0);
        when(assetClosureQueryAdapter.findAncestors(50L)).thenReturn(List.of(rootClosure));

        CreateAssetResponseDto result = service.createAsset(dto);

        assertThat(result.getParentId()).isEqualTo(50L);

        verify(assetClosureJpaRepository, atLeastOnce()).save(any(AssetClosure.class));
    }

    // -------------------------------------------------------
    // UPDATE ASSET
    // -------------------------------------------------------
    @Test
    @DisplayName("자원 수정 - 이름 중복 예외")
    void updateAsset_duplicateName() {

        // 기존 자원 (원래 이름은 "기존이름"이라고 가정)
        Asset asset = mock(Asset.class);
        when(asset.getName()).thenReturn("기존이름");

        // getAssetById(assetId) → 위 asset 반환
        when(assetJpaRepository.findById(10L)).thenReturn(Optional.of(asset));

        // dto: 변경할 이름이 "프린터"라서 이름이 변경됨
        UpdateAssetRequestDto dto = UpdateAssetRequestDto.builder()
                .categoryId(1L)
                .name("프린터") // 변경된 이름
                .description("desc")
                .image(null)
                .status("AVAILABLE")
                .type("DEVICE")
                .accessLevel(1)
                .approvalStatus(true)
                .costPerHour(BigDecimal.TEN)
                .periodCost(BigDecimal.ONE)
                .version(0L)
                .build();

        // 중복되는 이름이라는 Mock 세팅
        when(assetJpaRepository.existsByName("프린터")).thenReturn(true);

        // 실행 & 검증
        assertThatThrownBy(() -> service.updateAsset(dto, 10L)).isInstanceOf(AssetException.class);
    }

    @Test
    @DisplayName("자원 수정 - 정상 수정 + category 검증 호출")
    void updateAsset_success() {

        UpdateAssetRequestDto dto = new UpdateAssetRequestDto(
                1L, "새이름", "설명변경", null, "AVAILABLE", "STATIC", 1, true, BigDecimal.TEN, BigDecimal.ONE, 0L);

        when(assetJpaRepository.existsByName("새이름")).thenReturn(false);

        doNothing().when(categoryCommandService).validateCategoryId(1L);

        Category category = Category.builder().name("카테고리").description("desc").build();
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(category));

        Asset asset = Asset.builder()
                .category(category)
                .name("원래이름")
                .description("oldDesc")
                .image(null)
                .status(AssetStatus.fromName("AVAILABLE").toCode())
                .type(AssetType.fromName("STATIC").toCode())
                .accessLevel(1)
                .needsApproval(true)
                .costPerHour(BigDecimal.TEN)
                .periodCost(BigDecimal.ONE)
                .build();
        when(assetJpaRepository.findById(10L)).thenReturn(Optional.of(asset));

        UpdateAssetResponseDto result = service.updateAsset(dto, 10L);

        verify(categoryCommandService).validateCategoryId(1L);
        // apply() 호출되었는지 대신 → 값이 실제로 변경되었는지 체크
        assertThat(asset.getName()).isEqualTo("새이름");
        assertThat(asset.getDescription()).isEqualTo("설명변경");
        assertThat(asset.getCategory().getId()).isEqualTo(1L);
    }

    // -------------------------------------------------------
    // DELETE ASSET
    // -------------------------------------------------------
    @Test
    @DisplayName("자원 삭제 - 자식 있으면 예외")
    void softDeleteAsset_hasChildren() {

        Asset asset = mock(Asset.class);
        when(assetJpaRepository.findById(10L)).thenReturn(Optional.of(asset));
        when(assetClosureQueryAdapter.existsChildren(10L)).thenReturn(true);

        when(userReader.findById(99L)).thenReturn(null);

        assertThatThrownBy(() -> service.softDeleteAsset(10L, 99L)).isInstanceOf(AssetException.class);
    }

    @Test
    @DisplayName("자원 삭제 - softDelete + closure 삭제 정상 흐름")
    void softDeleteAsset_success() {

        Asset asset = mock(Asset.class);

        when(assetJpaRepository.findById(10L)).thenReturn(Optional.of(asset));
        when(assetClosureQueryAdapter.existsChildren(10L)).thenReturn(false);
        when(userReader.findById(99L)).thenReturn(null);

        service.softDeleteAsset(10L, 99L);

        verify(asset).softDelete(99L);
        verify(assetJpaRepository).save(asset);

        // 현재 서비스는 descendant 삭제만 수행함
        verify(assetClosureQueryAdapter).deleteAllByDescendantId(10L);
    }
}
