package com.beyond.qiin.domain.booking.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.time.LocalDate;
import java.util.ArrayList;
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
        LocalDate date = LocalDate.of(2025, 12, 4);
        GetAppliedReservationSearchCondition condition = new GetAppliedReservationSearchCondition();
        condition.setDate(date);

        User user = User.builder().userName("A").email("A@gmail.com").build();

        Pageable pageable = PageRequest.of(0, 10);

        // Mock userReader
        when(userReader.findById(userId)).thenReturn(user);

        // Mock repository 검색 결과

        RawAppliedReservationResponseDto raw1 = new RawAppliedReservationResponseDto(
                10L, // assetId
                "Projector", // assetName
                1L, // reservationId
                "Alice", // applicantName
                "Bob", // respondentName
                1, // reservationStatus
                true, // isApproved
                "Projector needed for presentation", // reason
                100L // version
                );

        RawAppliedReservationResponseDto raw2 = new RawAppliedReservationResponseDto(
                20L, // assetId
                "Laptop", // assetName
                2L, // reservationId
                "Charlie", // applicantName
                "David", // respondentName
                0, // reservationStatus
                false, // isApproved
                "Laptop needed for report", // reason
                101L // version
                );

        List<RawAppliedReservationResponseDto> rawList = new ArrayList<>();
        rawList.add(raw1);
        rawList.add(raw2);

        when(appliedReservationsQueryRepository.search(condition)).thenReturn(rawList);

        // 실행
        PageResponseDto<GetAppliedReservationResponseDto> result =
                reservationQueryService.getReservationApplies(userId, condition, pageable);

        // 검증
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
    }
}
