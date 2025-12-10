package com.beyond.qiin.domain.inventory.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.MoveAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.*;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import com.beyond.qiin.security.config.SecurityConfig;
import com.beyond.qiin.security.jwt.JwtFilter;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.jwt.RedisTokenRepository;
import com.beyond.qiin.security.resolver.ArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = AssetController.class,
        excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssetCommandService assetCommandService;

    @MockBean
    private AssetQueryService assetQueryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisTokenRepository redisTokenRepository;

    @MockBean
    private ArgumentResolver argumentResolver;

    // ----------------------------------------------------------
    // 1. CREATE
    // ----------------------------------------------------------
    @Test
    @DisplayName("자원 생성 성공 - Builder 기반")
    void createAsset_success() throws Exception {

        CreateAssetRequestDto request = CreateAssetRequestDto.builder()
                .name("노트북1")
                .categoryId(2L)
                .parentName("전자기기")
                .description("개발 장비")
                .status("AVAILABLE")
                .type("DEVICE")
                .accessLevel(1)
                .approvalStatus(true)
                .costPerHour(BigDecimal.valueOf(1000))
                .periodCost(BigDecimal.valueOf(2000))
                .build();

        CreateAssetResponseDto response = CreateAssetResponseDto.builder()
                .assetId(10L)
                .parentId(1L)
                .categoryId(2L)
                .name("노트북1")
                .description("개발 장비")
                .image(null)
                .status(0)
                .type(1)
                .accessLevel(1)
                .approvalStatus(true)
                .costPerHour(BigDecimal.valueOf(1000))
                .periodCost(BigDecimal.valueOf(2000))
                .createdAt(Instant.now())
                .createdBy(99L)
                .build();

        when(assetCommandService.createAsset(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assetId").value(10L))
                .andExpect(jsonPath("$.categoryId").value(2L))
                .andExpect(jsonPath("$.name").value("노트북1"));
    }

    // ----------------------------------------------------------
    // 2. UPDATE
    // ----------------------------------------------------------
    @Test
    @DisplayName("자원 수정 성공")
    void updateAsset_success() throws Exception {

        UpdateAssetRequestDto request = UpdateAssetRequestDto.builder()
                .name("노트북2")
                .description("설명 수정됨")
                .status("MAINTENANCE")
                .type("DEVICE")
                .accessLevel(2)
                .build();

        UpdateAssetResponseDto response = UpdateAssetResponseDto.builder()
                .assetId(10L)
                .name("노트북2")
                .description("설명 수정됨")
                .status(2)
                .type(1)
                .accessLevel(2)
                .build();

        when(assetCommandService.updateAsset(any(), eq(10L))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/assets/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetId").value(10L))
                .andExpect(jsonPath("$.name").value("노트북2"));
    }

    // ----------------------------------------------------------
    // 3. DELETE
    // ----------------------------------------------------------
    @Test
    @DisplayName("자원 삭제 성공")
    void deleteAsset_success() throws Exception {

        when(jwtTokenProvider.getUserId(anyString())).thenReturn(99L);

        mockMvc.perform(delete("/api/v1/assets/5").header("Authorization", "Bearer test-token"))
                .andExpect(status().isNoContent());
    }

    // ----------------------------------------------------------
    // 4. MOVE ASSET
    // ----------------------------------------------------------
    @Test
    @DisplayName("자원 이동 성공")
    void moveAsset_success() throws Exception {

        MoveAssetRequestDto request =
                MoveAssetRequestDto.builder().parentName("회의실A").build();

        mockMvc.perform(patch("/api/v1/assets/5/move")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // ----------------------------------------------------------
    // 5. ROOT 조회
    // ----------------------------------------------------------
    @Test
    @DisplayName("0계층 조회 성공")
    void getRootAssets_success() throws Exception {

        List<RootAssetResponseDto> list = List.of(
                RootAssetResponseDto.builder().assetId(1L).name("본관").build(),
                RootAssetResponseDto.builder().assetId(2L).name("신관").build());

        when(assetQueryService.getRootAssetIds()).thenReturn(list);

        mockMvc.perform(get("/api/v1/assets/roots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("본관"));
    }

    // ----------------------------------------------------------
    // 6. ONE-DEPTH 조회
    // ----------------------------------------------------------
    @Test
    @DisplayName("1계층 조회 성공")
    void getOneDepth_success() throws Exception {

        List<OneDepthAssetResponseDto> list = List.of(
                OneDepthAssetResponseDto.builder().assetId(10L).name("회의실1").build(),
                OneDepthAssetResponseDto.builder().assetId(11L).name("회의실2").build());

        when(assetQueryService.getOneDepthAssetList(1L)).thenReturn(list);

        mockMvc.perform(get("/api/v1/assets/1/one-depth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("회의실1"));
    }

    // ----------------------------------------------------------
    // 7. DESCENDANTS 조회
    // ----------------------------------------------------------
    @Test
    @DisplayName("예약 가능한 자원 조회 성공")
    void getDescendants_success() throws Exception {

        List<DescendantAssetResponseDto> items = List.of(DescendantAssetResponseDto.builder()
                .assetId(100L)
                .name("노트북1")
                .categoryName("전자기기")
                .status("사용 가능")
                .type("장비")
                .needApproval(false)
                .reservable(true)
                .version(1L)
                .build());

        PageResponseDto<DescendantAssetResponseDto> page =
                PageResponseDto.from(new PageImpl<>(items, PageRequest.of(0, 10), 1));

        when(assetQueryService.getDescendantAssetList(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/assets/descendants?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("노트북1"));
    }

    // ----------------------------------------------------------
    // 8. TREE 조회
    // ----------------------------------------------------------
    @Test
    @DisplayName("자원 전체 트리 조회 성공")
    void getTree_success() throws Exception {

        List<TreeAssetResponseDto> tree = List.of(TreeAssetResponseDto.builder()
                .assetId(1L)
                .name("본관")
                .children(List.of())
                .build());

        when(assetQueryService.getFullAssetTree()).thenReturn(tree);

        mockMvc.perform(get("/api/v1/assets/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("본관"));
    }

    // ----------------------------------------------------------
    // 9. DETAIL 조회
    // ----------------------------------------------------------
    @Test
    @DisplayName("자원 상세 조회 성공")
    void getAssetDetail_success() throws Exception {

        AssetDetailResponseDto detail = AssetDetailResponseDto.builder()
                .assetId(100L)
                .name("빔프로젝터")
                .description("대회의실 장비")
                .image(null)
                .status("AVAILABLE")
                .type("DEVICE")
                .accessLevel(1)
                .approvalStatus(true)
                .categoryName("전자기기")
                .build();

        when(assetQueryService.getAssetDetail(100L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/assets/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("빔프로젝터"));
    }
}
