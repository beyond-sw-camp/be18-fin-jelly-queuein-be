package com.beyond.qiin.domain.accounting.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.accounting.entity.UserHistory;
import com.beyond.qiin.domain.accounting.repository.UserHistoryJpaRepository;
import com.beyond.qiin.domain.booking.entity.Attendant;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.iam.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserHistoryCommandServiceImpl 단위 테스트")
class UserHistoryCommandServiceImplTest {

    private UserHistoryCommandServiceImpl service;
    private UserHistoryJpaRepository userHistoryJpaRepository;

    @BeforeEach
    void setUp() {
        userHistoryJpaRepository = mock(UserHistoryJpaRepository.class);
        service = new UserHistoryCommandServiceImpl(userHistoryJpaRepository);
    }

    /**
     * Helper: Mock User 생성
     */
    private User createMockUser(Long userId) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        return user;
    }

    /**
     * Helper: Mock Attendant 생성
     */
    private Attendant createMockAttendant(Long userId) {
        Attendant attendant = mock(Attendant.class);
        User user = createMockUser(userId);
        when(attendant.getUser()).thenReturn(user);
        return attendant;
    }

    // -----------------------------------------------
    // 1. 성공 케이스: 참석자가 있을 때
    // -----------------------------------------------

    @Test
    @DisplayName("createUserHistories - 참석자 수만큼 UserHistory 생성 및 저장 성공")
    void createUserHistories_withAttendants_success() {
        // Given
        Reservation mockReservation = mock(Reservation.class);
        UsageHistory mockUsageHistory = mock(UsageHistory.class);

        // 참석자 2명 설정
        List<Attendant> attendants = List.of(createMockAttendant(101L), createMockAttendant(102L));
        when(mockReservation.getAttendants()).thenReturn(attendants);

        // When
        service.createUserHistories(mockReservation, mockUsageHistory);

        // Then
        // 2명의 참석자 수만큼 save 메서드가 호출되었는지 검증
        verify(userHistoryJpaRepository, times(2)).save(any(UserHistory.class));

        // save에 전달된 UserHistory 객체들이 올바른 usageHistory를 참조하는지 간접 검증
        verify(userHistoryJpaRepository, times(2))
                .save(argThat(history -> history.getUsageHistory() == mockUsageHistory));
    }

    // -----------------------------------------------
    // 2. 엣지 케이스: 참석자 목록이 Null일 때
    // -----------------------------------------------

    @Test
    @DisplayName("createUserHistories - 참석자 목록이 Null이면 저장 로직 실행 안됨")
    void createUserHistories_attendantsNull_noSave() {
        // Given
        Reservation mockReservation = mock(Reservation.class);
        UsageHistory mockUsageHistory = mock(UsageHistory.class);

        // 참석자 목록이 Null
        when(mockReservation.getAttendants()).thenReturn(null);

        // When
        service.createUserHistories(mockReservation, mockUsageHistory);

        // Then
        // save 메서드가 호출되지 않았는지 검증
        verify(userHistoryJpaRepository, never()).save(any(UserHistory.class));
    }

    // -----------------------------------------------
    // 3. 엣지 케이스: 참석자 목록이 비어있을 때
    // -----------------------------------------------

    @Test
    @DisplayName("createUserHistories - 참석자 목록이 비어있으면 저장 로직 실행 안됨")
    void createUserHistories_attendantsEmpty_noSave() {
        // Given
        Reservation mockReservation = mock(Reservation.class);
        UsageHistory mockUsageHistory = mock(UsageHistory.class);

        // 참석자 목록이 Empty List
        when(mockReservation.getAttendants()).thenReturn(List.of());

        // When
        service.createUserHistories(mockReservation, mockUsageHistory);

        // Then
        // save 메서드가 호출되지 않았는지 검증
        verify(userHistoryJpaRepository, never()).save(any(UserHistory.class));
    }
}
