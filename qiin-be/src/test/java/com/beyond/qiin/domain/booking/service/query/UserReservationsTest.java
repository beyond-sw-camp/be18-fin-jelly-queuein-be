package com.beyond.qiin.domain.booking.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.user_reservation.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.repository.querydsl.UserReservationsQueryRepository;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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
public class UserReservationsTest {
    @InjectMocks
    private ReservationQueryServiceImpl reservationQueryService;

    @Mock
    private UserReader userReader;

    @Mock
    private UserReservationsQueryRepository userReservationsQueryRepository;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private AssetQueryService assetQueryService;

    @Test
    void getReservationsByUserId_returnsPagedDto() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 12, 4);
        GetUserReservationSearchCondition condition = new GetUserReservationSearchCondition();
        condition.setDate(date);

        User user = User.builder().userName("A").email("A@gmail.com").build();

        when(userReader.findById(userId)).thenReturn(user);

        ZoneId zone = ZoneId.of("Asia/Seoul");

        RawUserReservationResponseDto raw1 = new RawUserReservationResponseDto(
                1L, // reservationId
                date.atTime(9, 0).atZone(zone).toInstant(), // startAt
                date.atTime(10, 0).atZone(zone).toInstant(), // endAt
                1, // reservationStatus
                true, // isApproved
                null, // actualStartAt
                null, // actualEndAt
                100L, // version
                10L, // assetId
                "Projector", // assetName
                "Electronics", // categoryName
                0, // assetType
                1 // assetStatus
                );

        RawUserReservationResponseDto raw2 = new RawUserReservationResponseDto(
                2L, // reservationId
                date.atTime(14, 0).atZone(zone).toInstant(), // startAt
                date.atTime(15, 0).atZone(zone).toInstant(), // endAt
                0, // reservationStatus
                false, // isApproved
                null, // actualStartAt
                null, // actualEndAt
                101L, // version
                20L, // assetId
                "Laptop", // assetName
                "Electronics", // categoryName
                1, // assetType
                1 // assetStatus
                );

        List<RawUserReservationResponseDto> rawList = List.of(raw1, raw2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<RawUserReservationResponseDto> rawPage = new PageImpl<>(rawList, pageable, rawList.size());

        when(userReservationsQueryRepository.search(userId, condition, pageable))
                .thenReturn(rawPage);

        PageResponseDto<GetUserReservationResponseDto> result =
                reservationQueryService.getReservationsByUserId(userId, condition, pageable);

        // 검증
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getReservationId());
        assertEquals("Laptop", result.getContent().get(1).getAssetName());
        assertEquals(2, result.getTotalElements());
    }
}
