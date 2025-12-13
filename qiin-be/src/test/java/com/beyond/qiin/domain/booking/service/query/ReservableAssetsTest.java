 package com.beyond.qiin.domain.booking.service.query;

 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.anyLong;
 import static org.mockito.Mockito.when;

 import com.beyond.qiin.common.dto.PageResponseDto;
 import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
 import com.beyond.qiin.domain.booking.dto.reservation.response.reservable_asset.ReservableAssetResponseDto;
 import com.beyond.qiin.domain.booking.repository.querydsl.UserReservationsQueryRepository;
 import com.beyond.qiin.domain.booking.support.ReservationReader;
 import com.beyond.qiin.domain.iam.entity.User;
 import com.beyond.qiin.domain.iam.support.user.UserReader;
 import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawDescendantAssetResponseDto;
 import com.beyond.qiin.domain.inventory.repository.querydsl.AssetQueryRepository;
 import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
 import java.time.LocalDate;
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
 public class ReservableAssetsTest {
    @InjectMocks
    private ReservationQueryServiceImpl reservationQueryService;

    @Mock
    private UserReader userReader;

    @Mock
    private AssetQueryRepository assetQueryRepository;

    @Mock
    private AssetQueryService assetQueryService;

    @Mock
    private UserReservationsQueryRepository userReservationsQueryRepository;

    @Mock
    private ReservationReader reservationReader;

    @Test
    void getReservableAssets_filtersCorrectlyAndPages() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2025, 12, 4);

        ReservableAssetSearchCondition condition = new ReservableAssetSearchCondition();
        condition.setDate(date);

        Pageable pageable = PageRequest.of(0, 10);

        when(userReader.findById(userId))
                .thenReturn(User.builder().userName("A").email("a@gmail.com").build());

        RawDescendantAssetResponseDto raw1 =
                new RawDescendantAssetResponseDto(10L, "Projector", "Electronics", 1, 1, true, true, 1L);

        RawDescendantAssetResponseDto raw2 =
                new RawDescendantAssetResponseDto(20L, "Laptop", "Electronics", 1, 1, false, true, 1L);

        Page<RawDescendantAssetResponseDto> rawPage = new PageImpl<>(List.of(raw1, raw2), pageable, 2);

        when(assetQueryRepository.searchDescendantsAsList(any())).thenReturn(List.of(raw1, raw2));
        when(assetQueryService.isAvailable(anyLong())).thenReturn(true);

        PageResponseDto<ReservableAssetResponseDto> result =
                reservationQueryService.getReservableAssets(userId, condition, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
    }
 }
