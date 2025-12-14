package com.beyond.qiin.domain.accounting.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.request.UsageHistoryListSearchRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryDetailResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_history.response.UsageHistoryListResponseDto;
import com.beyond.qiin.domain.accounting.repository.UsageHistoryJpaRepository;
import com.beyond.qiin.domain.accounting.repository.querydsl.UsageHistoryQueryAdapter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UsageHistoryQueryServiceImpl 단위 테스트")
class UsageHistoryQueryServiceImplTest {

    private UsageHistoryQueryServiceImpl service;
    private UsageHistoryQueryAdapter usageHistoryQueryAdapter;

    @BeforeEach
    void setUp() {
        usageHistoryQueryAdapter = mock(UsageHistoryQueryAdapter.class);
        UsageHistoryJpaRepository usageHistoryJpaRepository = mock(UsageHistoryJpaRepository.class);

        service = new UsageHistoryQueryServiceImpl(usageHistoryQueryAdapter, usageHistoryJpaRepository);
    }

    // -----------------------------------------------
    // 1. 사용 내역 목록 조회 (getUsageHistoryList)
    // -----------------------------------------------

    @Test
    @DisplayName("getUsageHistoryList - 어댑터 호출 및 결과 그대로 반환 검증")
    void getUsageHistoryList_returnsAdapterResult() {
        // Given
        UsageHistoryListSearchRequestDto req = new UsageHistoryListSearchRequestDto();
        ReflectionTestUtils.setField(req, "keyword", "Test Asset");

        Pageable pageable = PageRequest.of(0, 10);

        // Mock Raw DTO 생성
        UsageHistoryListResponseDto mockDto = new UsageHistoryListResponseDto(
                1L,
                "Server-A",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                60,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                55,
                new BigDecimal("0.916"));

        // Mock Page 객체 생성
        List<UsageHistoryListResponseDto> mockContent = List.of(mockDto);
        Page<UsageHistoryListResponseDto> mockPage = new PageImpl<>(mockContent, pageable, 100);

        // Adapter Mocking: Mock Page를 반환하도록 설정
        when(usageHistoryQueryAdapter.searchUsageHistory(eq(req), eq(pageable))).thenReturn(mockPage);

        // When
        PageResponseDto<UsageHistoryListResponseDto> result = service.getUsageHistoryList(req, pageable);

        // Then
        // 1. 어댑터가 올바른 인자로 호출되었는지 확인
        verify(usageHistoryQueryAdapter).searchUsageHistory(eq(req), eq(pageable));

        // 2. 결과가 PageResponseDto로 올바르게 변환되었는지 확인
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(100);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAssetName()).isEqualTo("Server-A");
    }

    // -----------------------------------------------
    // 2. 사용 내역 상세 조회 (getUsageHistoryDetail)
    // -----------------------------------------------

    @Test
    @DisplayName("getUsageHistoryDetail - 어댑터 호출 및 상세 DTO 반환 검증")
    void getUsageHistoryDetail_returnsAdapterResult() {
        // Given
        final Long historyId = 42L;

        // Mock 상세 DTO 생성
        UsageHistoryDetailResponseDto mockDetailDto = UsageHistoryDetailResponseDto.builder()
                .usageHistoryId(historyId)
                .assetName("GPU-Cluster")
                .reserverNames(List.of("Alice", "Bob"))
                .billAmount(new BigDecimal("100.00"))
                .actualBillAmount(new BigDecimal("95.00"))
                .build();

        // Adapter Mocking: Mock 상세 DTO를 반환하도록 설정
        when(usageHistoryQueryAdapter.getUsageHistoryDetail(historyId)).thenReturn(mockDetailDto);

        // When
        UsageHistoryDetailResponseDto result = service.getUsageHistoryDetail(historyId);

        // Then
        // 1. 어댑터가 올바른 ID로 호출되었는지 확인
        verify(usageHistoryQueryAdapter).getUsageHistoryDetail(historyId);

        // 2. 결과 DTO의 내용이 올바르게 반환되었는지 확인
        assertThat(result).isNotNull();
        assertThat(result.getUsageHistoryId()).isEqualTo(historyId);
        assertThat(result.getAssetName()).isEqualTo("GPU-Cluster");
        assertThat(result.getReserverNames()).hasSize(2);
    }
}
