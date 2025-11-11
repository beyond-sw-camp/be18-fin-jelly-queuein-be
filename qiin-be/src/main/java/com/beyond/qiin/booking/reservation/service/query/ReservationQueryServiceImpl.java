package com.beyond.qiin.booking.reservation.service.query;

import com.beyond.qiin.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.booking.reservation.entity.Reservation;
import com.beyond.qiin.booking.reservation.repository.ReservationJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationQueryServiceImpl implements ReservationQueryService {
  private final ReservationJpaRepository reservationJpaRepository;

  //자원 자체 (예외처리 포함)
  @Override
  public Reservation getReservationById(Long id){
    Reservation reservation = reservationJpaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("reservation not found by id"));
    return reservation;
  }

  //자원 목록 (예외처리 포함)
  @Override
  public List<Reservation> getReservationsByAssetId(Long assetId){
    List<Reservation> reservations = reservationJpaRepository.findByAssetId(assetId);
    if(reservations.isEmpty()){
      throw new EntityNotFoundException("Reservation not found by asset id: " + assetId);
    }
    return reservations;
  }

  //사용자 이름으로 예약 목록 조회
  @Override
  public List<Reservation> getReservationsByUserId(Long userId){
    List<Reservation> reservations = reservationJpaRepository.findByUserId(userId);
    if(reservations.isEmpty()){
      throw new EntityNotFoundException("Reservation not found by user id: " + userId);
    }
    return reservations;
  }

  //예약 상세 조회 (api용)
  @Override
  public Reservation getReservationById(Long id){
    Reservation reservation = reservationJpaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("reservation not found by id"));

    ReservationResponseDto reservationResponseDto = ReservationResponseDto.builder()

        .build();

    return reservationResponseDto;
  }

  //예약 가능 자원 목록 조회
  @Override
  public List<Reservation> getReservationsByAssetId(Long assetId){
    List<Reservation> reservations = reservationJpaRepository.findByAssetId(assetId);
    if(reservations.isEmpty()){
      throw new EntityNotFoundException("Reservation not found by asset id: " + assetId);
    }
    return reservations;
  }

  //예약 신청 자원 승인 / 거절 조회

  //뭔가 조회가 그건데 흐음


  //일별 일정 조회
  //이거는 일별이니까 그것도 조금
  

  //신청 예약 목록 조회(관리자용)
  @Override
  public List<Reservation> getReservationApplies(){
    //status == pending인 경우
    List<Reservation> reservations = reservationJpaRepository.findAll();
    if(reservations.isEmpty()){
      throw new EntityNotFoundException("Reservation not found by asset id: " + assetId);
    }
    return reservations;
  }


}
