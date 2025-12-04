package com.beyond.qiin.domain.booking.service.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.reservable_asset.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.repository.querydsl.ReservableAssetsQueryRepository;
import com.beyond.qiin.domain.booking.repository.querydsl.UserReservationsQueryRepository;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
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
public class ReservableAssetsTest {
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

    @Mock
    private ReservableAssetsQueryRepository reservableAssetsQueryRepository;

    @Test
    void getReservableAssets_filtersCorrectlyAndPages() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 12, 4);
        ReservableAssetSearchCondition condition = new ReservableAssetSearchCondition();
        condition.setDate(date);

        Pageable pageable = PageRequest.of(0, 10);

        // Mock user 존재 확인
        when(userReader.findById(userId))
                .thenReturn(User.builder().userName("A").email("a@gmail.com").build());

        // Mock raw 데이터
        RawReservableAssetResponseDto raw1 = new RawReservableAssetResponseDto(
                10L, // assetId
                "Projector", // assetName
                1, // assetType
                "Electronics", // categoryName
                true // needsApproval
                );

        RawReservableAssetResponseDto raw2 = new RawReservableAssetResponseDto(
                20L, // assetId
                "Laptop", // assetName
                1, // assetType
                "Electronics", // categoryName
                false // needsApproval
                );
        List<RawReservableAssetResponseDto> rawList = List.of(raw1, raw2);
        when(reservableAssetsQueryRepository.search(condition)).thenReturn(rawList);

        // assetQueryService.isAvailable 제어
        when(assetQueryService.isAvailable(10L)).thenReturn(true);
        when(assetQueryService.isAvailable(20L)).thenReturn(true);

        // 실행
        PageResponseDto<ReservableAssetResponseDto> result =
                reservationQueryService.getReservableAssets(userId, condition, pageable);

        // 검증
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
    }
}
