package com.beyond.qiin.domain.accounting.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.dto.usage_target.request.UsageTargetCreateRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;
import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import com.beyond.qiin.domain.accounting.exception.UsageTargetException;
import com.beyond.qiin.domain.accounting.repository.UsageTargetJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UsageTargetCommandServiceImpl 단위 테스트")
class UsageTargetCommandServiceImplTest {

    private UsageTargetCommandServiceImpl service;
    private UsageTargetJpaRepository usageTargetJpaRepository;

    private static final int CURRENT_YEAR = 2025;
    private static final Long TEST_USER_ID = 1L;
    private static final String MOCKED_CREATED_AT_STRING = "2025-01-01T10:00:00Z";

    @BeforeEach
    void setUp() {
        usageTargetJpaRepository = mock(UsageTargetJpaRepository.class);
        service = new UsageTargetCommandServiceImpl(usageTargetJpaRepository);
    }

    /**
     * Helper: UsageTarget Mock Entity 생성 및 Getter 설정 (NullPointerException 방지 로직 포함)
     */
    UsageTarget createMockUsageTargetEntity(Long id, int year, BigDecimal rate, Long createdBy) {
        // Mock 객체 생성
        UsageTarget mockEntity = mock(UsageTarget.class);

        // Mocking 필수 Getter
        when(mockEntity.getId()).thenReturn(id);
        when(mockEntity.getYear()).thenReturn(year);
        when(mockEntity.getTargetRate()).thenReturn(rate);
        when(mockEntity.getCreatedBy()).thenReturn(createdBy);

        // **NullPointerException 해결 핵심:**
        // 1. getCreatedAt() 호출 시, Mockito Instant 객체를 반환합니다.
        Instant mockInstant = mock(Instant.class);
        when(mockEntity.getCreatedAt()).thenReturn(mockInstant);

        // 2. 해당 Mock Instant 객체의 toString() 호출 시, 정해진 문자열을 반환하도록 설정합니다.
        when(mockInstant.toString()).thenReturn(MOCKED_CREATED_AT_STRING);

        return mockEntity;
    }

    // -----------------------------------------------
    // 1. 목표 생성 성공 테스트 (NullPointerException 해결)
    // -----------------------------------------------

    @Test
    @DisplayName("createTarget - 올해 목표가 없을 경우 생성 성공")
    void createTarget_success() {
        final LocalDate fixedDate = LocalDate.of(CURRENT_YEAR, 1, 1);

        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            // Given
            mockedStatic.when(LocalDate::now).thenReturn(fixedDate);

            UsageTargetCreateRequestDto request = new UsageTargetCreateRequestDto();
            ReflectionTestUtils.setField(request, "targetRate", 0.90);
            BigDecimal targetRate = new BigDecimal("0.90");

            when(usageTargetJpaRepository.existsByYear(CURRENT_YEAR)).thenReturn(false);

            // Mock 객체 생성
            UsageTarget savedEntity = createMockUsageTargetEntity(10L, CURRENT_YEAR, targetRate, TEST_USER_ID);

            // save() 호출 시 Mock 엔티티 반환
            // save()의 인자로 전달된 UsageTarget 객체가 'UsageTarget.create()'로 생성된 객체인지 확인은
            // argThat으로 복잡하게 처리하는 대신, 반환되는 객체의 필드값만 검증합니다.
            when(usageTargetJpaRepository.save(any(UsageTarget.class))).thenReturn(savedEntity);

            // When
            UsageTargetResponseDto result = service.createTarget(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getYear()).isEqualTo(CURRENT_YEAR);
            // Mocking된 문자열로 반환되는지 확인
            assertThat(result.getCreatedAt()).isEqualTo(MOCKED_CREATED_AT_STRING);

            verify(usageTargetJpaRepository).existsByYear(CURRENT_YEAR);
            verify(usageTargetJpaRepository).save(any(UsageTarget.class));
        }
    }

    // -----------------------------------------------
    // 2. 목표 생성 실패 (이미 존재) 테스트
    // -----------------------------------------------

    @Test
    @DisplayName("createTarget - 이미 목표가 존재할 경우 UsageTargetException 발생")
    void createTarget_alreadyExists_fail() {
        final LocalDate fixedDate = LocalDate.of(CURRENT_YEAR, 1, 1);

        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            // Given
            mockedStatic.when(LocalDate::now).thenReturn(fixedDate);

            UsageTargetCreateRequestDto request = new UsageTargetCreateRequestDto();
            ReflectionTestUtils.setField(request, "targetRate", 0.85);

            when(usageTargetJpaRepository.existsByYear(CURRENT_YEAR)).thenReturn(true);

            // When & Then
            UsageTargetException exception =
                    assertThrows(UsageTargetException.class, () -> service.createTarget(request));

            // Assertion 수정: 실제 반환되는 한국어 메시지 검증
            assertThat(exception.getMessage()).contains("이미 올해 목표 사용률이 등록되었습니다.");

            // Verify: save는 호출되지 않았는지 확인
            verify(usageTargetJpaRepository).existsByYear(CURRENT_YEAR);
            verify(usageTargetJpaRepository, never()).save(any());
        }
    }
}
