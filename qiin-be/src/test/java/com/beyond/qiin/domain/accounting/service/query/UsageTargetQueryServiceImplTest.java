package com.beyond.qiin.domain.accounting.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetStatusResponseDto;
import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import com.beyond.qiin.domain.accounting.exception.UsageTargetException;
import com.beyond.qiin.domain.accounting.repository.UsageTargetJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@DisplayName("UsageTargetQueryServiceImpl 단위 테스트")
class UsageTargetQueryServiceImplTest {

    private UsageTargetQueryServiceImpl service;
    private UsageTargetJpaRepository usageTargetJpaRepository;

    private final int FIXED_YEAR = 2025;
    private final Long TEST_USER_ID = 99L;
    private final BigDecimal TEST_RATE = new BigDecimal("0.75");
    private final String MOCKED_CREATED_AT_STRING = "2025-01-01T00:00:00Z";

    @BeforeEach
    void setUp() {
        usageTargetJpaRepository = mock(UsageTargetJpaRepository.class);
        service = new UsageTargetQueryServiceImpl(usageTargetJpaRepository);
    }

    /**
     * Helper: UsageTarget Mock Entity 생성 (Null 안정성 확보)
     */
    UsageTarget createMockUsageTargetEntity(Long id, int year, BigDecimal rate, Long createdBy) {
        // Mock 객체를 생성하여 Getter와 toString()을 제어
        UsageTarget mockEntity = mock(UsageTarget.class);

        when(mockEntity.getId()).thenReturn(id);
        when(mockEntity.getYear()).thenReturn(year);
        when(mockEntity.getTargetRate()).thenReturn(rate);
        when(mockEntity.getCreatedBy()).thenReturn(createdBy);

        // NullPointerException 회피를 위해 getCreatedAt().toString()을 Mocking
        Instant mockInstant = mock(Instant.class);
        when(mockEntity.getCreatedAt()).thenReturn(mockInstant);
        when(mockInstant.toString()).thenReturn(MOCKED_CREATED_AT_STRING);

        return mockEntity;
    }

    // -----------------------------------------------
    // 1. 올해 목표 존재 여부 조회 (getCurrentYearStatus)
    // -----------------------------------------------

    @Test
    @DisplayName("getCurrentYearStatus - 목표 존재 시 exists=true 반환")
    void getCurrentYearStatus_exists() {
        final LocalDate fixedDate = LocalDate.of(FIXED_YEAR, 1, 1);

        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now).thenReturn(fixedDate);

            // Given: 올해(2025) 목표가 존재함
            UsageTarget mockEntity = createMockUsageTargetEntity(1L, FIXED_YEAR, TEST_RATE, TEST_USER_ID);
            when(usageTargetJpaRepository.findByYear(FIXED_YEAR)).thenReturn(Optional.of(mockEntity));

            // When
            UsageTargetStatusResponseDto result = service.getCurrentYearStatus();

            // Then
            assertThat(result.isExists()).isTrue();
            assertThat(result.getYear()).isEqualTo(FIXED_YEAR);
            assertThat(result.getTargetRate()).isEqualByComparingTo(TEST_RATE);

            verify(usageTargetJpaRepository).findByYear(FIXED_YEAR);
        }
    }

    @Test
    @DisplayName("getCurrentYearStatus - 목표 미존재 시 exists=false 반환")
    void getCurrentYearStatus_notExists() {
        final LocalDate fixedDate = LocalDate.of(FIXED_YEAR, 1, 1);

        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now).thenReturn(fixedDate);

            // Given: 올해(2025) 목표가 존재하지 않음
            when(usageTargetJpaRepository.findByYear(FIXED_YEAR)).thenReturn(Optional.empty());

            // When
            UsageTargetStatusResponseDto result = service.getCurrentYearStatus();

            // Then
            assertThat(result.isExists()).isFalse();
            assertThat(result.getYear()).isEqualTo(FIXED_YEAR);
            assertThat(result.getTargetRate()).isNull();

            verify(usageTargetJpaRepository).findByYear(FIXED_YEAR);
        }
    }

    // -----------------------------------------------
    // 2. 특정 연도 조회 (getByYear)
    // -----------------------------------------------

    @Test
    @DisplayName("getByYear - 특정 연도 목표 조회 성공")
    void getByYear_success() {
        // Given
        int targetYear = 2023;
        UsageTarget mockEntity = createMockUsageTargetEntity(5L, targetYear, TEST_RATE, TEST_USER_ID);
        when(usageTargetJpaRepository.findByYear(targetYear)).thenReturn(Optional.of(mockEntity));

        // When
        UsageTargetResponseDto result = service.getByYear(targetYear);

        // Then
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getYear()).isEqualTo(targetYear);
        assertThat(result.getTargetRate()).isEqualByComparingTo(TEST_RATE);
        assertThat(result.getCreatedAt()).isEqualTo(MOCKED_CREATED_AT_STRING);

        verify(usageTargetJpaRepository).findByYear(targetYear);
    }

    @Test
    @DisplayName("getByYear - 목표 미존재 시 UsageTargetException::notFound 예외 발생")
    void getByYear_notFound_throwsException() {
        // Given
        int targetYear = 2020;
        when(usageTargetJpaRepository.findByYear(targetYear)).thenReturn(Optional.empty());

        // When & Then
        // UsageTargetException::notFound 예외가 발생하는지 확인
        assertThrows(UsageTargetException.class, () -> service.getByYear(targetYear));

        verify(usageTargetJpaRepository).findByYear(targetYear);
    }
}
