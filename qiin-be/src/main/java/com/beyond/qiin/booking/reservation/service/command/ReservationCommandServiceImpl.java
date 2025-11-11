package com.beyond.qiin.booking.reservation.service.command;

import com.beyond.qiin.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.booking.dto.reservation.response.CreateReservationResponseDto;
import com.beyond.qiin.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.booking.reservation.entity.Reservation;
import com.beyond.qiin.booking.reservation.repository.ReservationJpaRepository;
import com.beyond.qiin.booking.reservation.service.query.ReservationQueryService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationCommandServiceImpl implements ReservationCommandService {

  private final ReservationQueryService reservationQueryService;
  private final ReservationJpaRepository reservationJpaRepository;

  //선착순 예약
  @Override
  @Transactional
  public CreateReservationResponseDto applyReservation(
      Long assetId,
      CreateReservationRequestDto createReservationRequestDto){

//    Asset asset = assetRepository.findByName(assetId)
//        .orElseThrow(() -> new EntityNotFoundException("asset not found by id"));

//    User applicant = userRepository.findByUserName(userId)
//        .orElseThrow(() -> new EntityNotFoundException("user not found by id"));
//

    Reservation reservation = Reservation.builder()
        .asset(asset)
        .applicant(applicant)
        .startAt(createReservationRequestDto.getStartAt())
        .endAt(createReservationRequestDto.getEndAt())
        .description(createReservationRequestDto.getDescription())
        .status(0) //PENDING
        .attendants(attendants)
        .build();

    List<Attendant> attendants = createReservationRequestDto.getAttendants().stream()
        .map(a -> Attendant.builder()
            .user(a.getUser())
            .reservation(reservation) // FK 연결!
            .build())
        .toList();

    reservation.setAttendants(attendants);
    reservationJpaRepository.save(reservation);

    CreateReservationResponseDto createReservationResponseDto = CreateReservationRequestDto.builder()
        .reservationId(reservation.getId())
        .assetName(asset.getName())
        .applicantName(applicant.getUserName())
        .startAt(reservation.getStartAt())
        .endAt(reservation.getEndAt())
        .description(reservation.getDescription())
        .status(reservation.getStatus())
        .attendants(attendants)
        .build();

    return createReservationResponseDto;
  }

  //예약 신청
  @Override
  @Transactional
  public CreateReservationResponseDto instantConfirmReservation(Long assetId, CreateReservationRequestDto createReservationRequestDto){


//    Asset asset = assetRepository.findByName(assetId)
//        .orElseThrow(() -> new EntityNotFoundException("asset not found by id"));

//    User applicant = userRepository.findByUserName(userId)
//        .orElseThrow(() -> new EntityNotFoundException("user not found by id"));
//

    if(!isReservationTimeAvailable(assetId, reservation.getStartAt(), reservation.getEndAt()))
      throw new IllegalArgumentException("reservation time duplicated");

    //해당 시간에 사용 가능한 자원인가

    //자원 자체가 지금 사용 가능한가

    Reservation reservation = Reservation.builder()
        .asset(asset)
        .applicants(applicants)
        .startAt(createReservationRequestDto.getStartAt())
        .endAt(createReservationRequestDto.getEndAt())
        .description(createReservationRequestDto.getDescription())
        .status(1) //APPROVED
        //참여자들
        .build();

    CreateReservationResponseDto createReservationResponseDto = CreateReservationRequestDto.builder()
        .reservationId(reservation.getId())
        .assetName(asset.getName())
        .applicantName(applicant.getUserName())
        .startAt(reservation.getStartAt())
        .endAt(reservation.getEndAt())
        .description(reservation.getDescription())
        .status(reservation.getStatus()) //APPROVED
        //참여자들
        .build();

    return createReservationResponseDto;
  }

  //비즈니스 책임은 command service로
  //자원에 대한 예약 가능의 유무
  private boolean isReservationTimeAvailable(Long assetId, Instant startAt, Instant endAt){
    //없어도 ㄱㅊ -> for문에 npe 생기나?
    List<Reservation> reservations = reservationQueryService.getReservationsByAssetId(assetId);

    //2-6인 경우 1-7, 2-4, 4-6, 3-4 모두 불가해야함
    for(Reservation reservation : reservations){
      Instant existingStart = reservation.getStartAt();
      Instant existingEnd = reservation.getEndAt();

      //둘 중 하나라도 달성되지 않으면 불가
      boolean overlaps = startAt.isBefore(existingEnd) && endAt.isAfter(existingStart);

      if (overlaps) {
        return false;
      }
    }
    return true;
  }

  //
  @Override
  @Transactional
  public void approveReservation(Long reservationId){
    //담당자 등급인 경우 승인 가능

    Reservation reservation = reservationQueryService.getReservationById(reservationId);
    Reservation updatedReservation = reservation.toBuilder()

        .status(1) //approved
        .build();

    reservationJpaRepository.save(updatedReservation); //객체 수정하는 tobuilder -> save 없이 동작하는지 확인

  }

  @Override
  @Transactional
  public void rejectReservation(Long reservationId){
    //담당자 등급인 경우 승인 가능

    Reservation reservation = reservationQueryService.getReservationById(reservationId);

    Reservation updatedReservation = reservation.toBuilder()
        .status(3) //rejected
        .build();

    reservationJpaRepository.save(updatedReservation);

  }

  @Override
  @Transactional
  public void startUsingReservation(Long reservationId, Instant startAt){
    //승인난 자원이라면

    //실제 시작 시간 추가
    Reservation reservation = reservationQueryService.getReservationById(reservationId);

    Reservation updatedReservation = reservation.toBuilder()
        .startAt(startAt)
        .status(2) //using
        .build();

    reservationJpaRepository.save(updatedReservation);
  }

  @Override
  @Transactional
  public void endUsingReservation(Long reservationId, Instant endAt){
    //사용중인 자원인 경우

    //실제 종료 시간 추가
    Reservation reservation = reservationQueryService.getReservationById(reservationId);

    Reservation updatedReservation = reservation.toBuilder()
        .endAt(endAt)
        .status(5) //complete
        .build();

    reservationJpaRepository.save(updatedReservation);

  }

  @Override
  @Transactional
  public void updateReservation(Long reservationId, UpdateReservationRequestDto updateReservationRequestDto){
    //예약 정보 변경
    Reservation reservation = reservationQueryService.getReservationById(reservationId);

    Reservation updatedReservation = reservation.toBuilder()
        .description(reservation.getDescription())
        //참여자
        .build();

    reservationJpaRepository.save(updatedReservation);
  }

  //사용 시간 30분 전인 경우 허용
  @Override
  @Transactional
  public void cancelReservation(Long reservationId){

    //getReservationById

    if(!reservationCancelAvailable(reservation))
      //ddd -> 검증 / service 의 행동 결정(메시지 던짐)
      throw new IllegalArgumentException("reservation time duplicated");

    //예약 취소
    Reservation reservation = reservationQueryService.getReservationById(reservationId);

    //reservation 상태 변경은 ddd로 엔티티 안에서 처리
    Reservation updatedReservation = reservation.toBuilder()
        .status(4) //canceled
        .build();

    reservationJpaRepository.save(updatedReservation);

  }

  private boolean reservationCancelAvailable(Reservation reservation){
    Instant now = Instant.now();
    Instant deadline = reservation.getStartAt().minus(30, ChronoUnit.MINUTES);

    if (now.isBefore(deadline)) {
      return true;  // 취소 가능
    }
    return false;
  }


}
