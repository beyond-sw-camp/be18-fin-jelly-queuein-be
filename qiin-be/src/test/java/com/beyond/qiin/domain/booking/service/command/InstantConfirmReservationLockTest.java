 package com.beyond.qiin.domain.booking.service.command;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.Mockito.doNothing;
 import static org.mockito.Mockito.times;
 import static org.mockito.Mockito.verify;
 import static org.mockito.Mockito.when;

 import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
 import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
 import com.beyond.qiin.domain.booking.enums.ReservationStatus;
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
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.springframework.data.redis.core.RedisTemplate;

 @ExtendWith(MockitoExtension.class)
 public class InstantConfirmReservationLockTest {


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

   private Long userId;
   private Long assetId;
   private Instant startAt;
   private Instant endAt;

   @BeforeEach
   void setUp() {
     MockitoAnnotations.openMocks(this);

     reservationCommandService = new ReservationCommandServiceImpl(
         userReader,
         reservationReader,
         reservationWriter,
         attendantWriter,
         assetCommandService,
         reservationEventPublisher,
         attendantJpaRepository
     );

     userId = 1L;
     assetId = 100L;
     startAt = Instant.parse("2025-12-04T10:00:00Z");
     endAt = Instant.parse("2025-12-04T11:00:00Z");
   }

   @Test
   void instantConfirmReservation_basicMockTest() {
     // Arrange
     CreateReservationRequestDto requestDto = CreateReservationRequestDto.builder()
         .startAt(startAt)
         .endAt(endAt)
         .attendantIds(List.of(2L, 3L))
         .build();

     User applicant = User.builder().userName("신청자").build();
     Asset asset = Asset.builder().name("회의실 A").build();
     List<User> attendants = List.of(
         User.builder().userName("참석자1").build(),
         User.builder().userName("참석자2").build()
     );

     // Mock 동작 정의
     when(userReader.findById(userId)).thenReturn(applicant);
     doNothing().when(userReader).validateAllExist(requestDto.getAttendantIds());
     when(userReader.findAllByIds(requestDto.getAttendantIds())).thenReturn(attendants);
     when(assetCommandService.getAssetById(assetId)).thenReturn(asset);
     doNothing().when(assetCommandService).isAvailable(assetId);
     doNothing().when(reservationWriter).save(any());
     doNothing().when(attendantWriter).saveAll(any());
     doNothing().when(reservationEventPublisher).publishCreated(any());

     // Act
     ReservationResponseDto result =
         reservationCommandService.instantConfirmReservation(userId, assetId, requestDto);

     // Assert
     assertThat(result).isNotNull();
     assertThat(result.getAssetName()).isEqualTo(asset.getName());
     assertThat(result.getStatus()).isEqualTo(ReservationStatus.APPROVED);
     assertThat(result.getIsApproved()).isTrue();

     // Verify 호출 확인
     verify(userReader).findById(userId);
     verify(userReader).validateAllExist(requestDto.getAttendantIds());
     verify(userReader).findAllByIds(requestDto.getAttendantIds());
     verify(assetCommandService).getAssetById(assetId);
     verify(assetCommandService).isAvailable(assetId);
     verify(reservationWriter).save(any());
     verify(attendantWriter).saveAll(any());
     verify(reservationEventPublisher).publishCreated(any());
   }
 }
