package com.beyond.qiin.domain.booking.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beyond.qiin.domain.accounting.service.command.UsageHistoryCommandService;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.event.ReservationEventPublisher;
import com.beyond.qiin.domain.booking.repository.AttendantJpaRepository;
import com.beyond.qiin.domain.booking.support.AttendantWriter;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InstantConfirmReservationTest {

    private ReservationCommandServiceImpl reservationCommandService;

    @Mock
    private UserReader userReader;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private ReservationWriter reservationWriter;

    @Mock
    private AttendantWriter attendantWriter;

    @Mock
    private AssetCommandService assetCommandService;

    @Mock
    private ReservationEventPublisher reservationEventPublisher;

    @Mock
    private AttendantJpaRepository attendantJpaRepository;

    @Mock
    private UsageHistoryCommandService usageHistoryCommandService;

    private Long userId;
    private Long assetId;
    private Instant startAt;
    private Instant endAt;

    @BeforeEach
    void setUp() {

        reservationCommandService = new ReservationCommandServiceImpl(
                userReader,
                reservationReader,
                reservationWriter,
                attendantWriter,
                assetCommandService,
                reservationEventPublisher,
                attendantJpaRepository,
                usageHistoryCommandService);

        userId = 1L;
        assetId = 100L;
        startAt = Instant.now().plusSeconds(3600); // 1시간 뒤
        endAt = Instant.now().plusSeconds(7200); // 2시간 뒤
    }

    @Test
    void instantConfirmReservation_basicMockTest() {
        // 요청 dto
        CreateReservationRequestDto requestDto = CreateReservationRequestDto.builder()
                .startAt(startAt)
                .endAt(endAt)
                .attendantIds(List.of(2L, 3L))
                .build();

        User applicant = User.builder().userName("신청자").build();
        Asset asset = Asset.builder().name("회의실 A").build();
        List<User> attendants = List.of(
                User.builder().userName("참석자1").build(),
                User.builder().userName("참석자2").build());

        when(assetCommandService.getAssetById(assetId)).thenReturn(asset);
        when(userReader.findById(userId)).thenReturn(applicant);
        doNothing().when(userReader).validateAllExist(requestDto.getAttendantIds());
        when(userReader.findAllByIds(requestDto.getAttendantIds())).thenReturn(attendants);
        when(assetCommandService.isAvailable(assetId)).thenReturn(true);

        //
        ReservationResponseDto result =
                reservationCommandService.instantConfirmReservation(userId, assetId, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getAssetName()).isEqualTo(asset.getName());
        assertEquals("APPROVED", result.getStatus());
        assertThat(result.getIsApproved()).isTrue();

        verify(userReader).findById(userId);
        verify(userReader).validateAllExist(requestDto.getAttendantIds());
        verify(userReader).findAllByIds(requestDto.getAttendantIds());
        verify(assetCommandService).getAssetById(assetId);
        verify(assetCommandService).isAvailable(assetId);
        verify(reservationWriter).save(any());
        verify(attendantWriter).saveAll(any());
        verify(reservationEventPublisher).publishEventCreated(any(), any());
    }
}
