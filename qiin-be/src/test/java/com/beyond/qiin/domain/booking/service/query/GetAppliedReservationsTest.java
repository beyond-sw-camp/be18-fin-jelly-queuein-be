package com.beyond.qiin.domain.booking.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.applied_reservation.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.repository.querydsl.AppliedReservationsQueryRepository;
import com.beyond.qiin.domain.booking.repository.querydsl.ReservableAssetsQueryRepository;
import com.beyond.qiin.domain.booking.repository.querydsl.UserReservationsQueryRepository;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class GetAppliedReservationsTest {

    @Mock
    private UserReader userReader;

    @Mock
    private AppliedReservationsQueryRepository appliedReservationsQueryRepository;

    @InjectMocks
    private ReservationQueryServiceImpl reservationQueryService;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private AssetQueryService assetQueryService;

    @Mock
    private UserReservationsQueryRepository userReservationsQueryRepository;

    @Mock
    private ReservableAssetsQueryRepository reservableAssetsQueryRepository;

    @Test
    void getReservationApplies_returnsPagedDto() {
        Long userId = 1L;

        GetAppliedReservationSearchCondition condition = new GetAppliedReservationSearchCondition();
        condition.setDate(LocalDate.of(2025, 12, 4));

        // Mock user
        User user = User.builder().userName("Alice").build();
        when(userReader.findById(userId)).thenReturn(user);

        // Mock raw data
        RawAppliedReservationResponseDto raw1 = new RawAppliedReservationResponseDto(
                10L,
                "Projector",
                1L,
                "Alice",
                "Bob",
                1,
                true,
                "Projector needed",
                100L,
                Instant.parse("2025-12-04T10:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));

        RawAppliedReservationResponseDto raw2 = new RawAppliedReservationResponseDto(
                20L,
                "Laptop",
                2L,
                "Charlie",
                "David",
                0,
                false,
                "Laptop needed",
                101L,
                Instant.parse("2025-12-04T13:00:00Z"),
                Instant.parse("2025-12-04T15:00:00Z"));

        List<RawAppliedReservationResponseDto> rawList = List.of(raw1, raw2);
        when(appliedReservationsQueryRepository.search(condition)).thenReturn(rawList);

        doReturn(true).when(reservationQueryService).isReservationTimeAvailable(anyLong(), anyLong(), any(), any());

        // Mock asset availability
        when(assetQueryService.isAvailable(anyLong())).thenReturn(true);

        Pageable pageable = PageRequest.of(0, 10);

        // 실행
        PageResponseDto<GetAppliedReservationResponseDto> result =
                reservationQueryService.getReservationApplies(userId, condition, pageable);

        // 검증
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());

        assertEquals("Projector", result.getContent().get(0).getAssetName());
        assertEquals("Laptop", result.getContent().get(1).getAssetName());
    }
}
