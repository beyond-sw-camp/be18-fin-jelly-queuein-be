package com.beyond.qiin.domain.booking.reservation.service.command;

import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.user_rev.entity.Attendant;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationException;
import com.beyond.qiin.domain.booking.reservation.repository.ReservationJpaRepository;
import com.beyond.qiin.domain.entity.User;
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

    private final ReservationJpaRepository reservationJpaRepository;

    // 선착순 예약
    @Override
    @Transactional
    public ReservationResponseDto applyReservation(
            final Long assetId, final CreateReservationRequestDto createReservationRequestDto) {

        Asset asset = assetJpaRepository
                .findByName(assetId)
                .orElseThrow(() -> new EntityNotFoundException("asset not found by id"));

        User applicant = userJpaRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found by id"));

        // 참여자 전원 있는지에 대한 확인

        List<Attendant> attendants = Attendant.create(users, reservation);

        // 자원 자체가 지금 사용 가능한가에 대한 확인
        if ((asset.getStatus() == 1) || (asset.getStatus() == 2))
            throw new IllegalArgumentException("asset not available");

        Reservation reservation = createReservationRequestDto.toEntity(asset, applicant, attendants, 0);

        reservationJpaRepository.save(reservation);

        return ReservationResponseDto.fromEntity(reservation, statusToString(0));
    }

    // 예약 신청
    @Override
    @Transactional
    public ReservationResponseDto instantConfirmReservation(
            final Long assetId, final CreateReservationRequestDto createReservationRequestDto) {

        Asset asset = assetRepository
                .findByName(assetId)
                .orElseThrow(() -> new EntityNotFoundException("asset not found by id"));

        User applicant =
                userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found by id"));

        // 참여자 목록의 사용자들이 모두 존재하는지에 대한 확인

        // 해당 시간에 사용 가능한 자원인지 확인
        validateReservationAvailability(
                asset.getId(), createReservationRequestDto.getStartAt(), createReservationRequestDto.getEndAt());

        // 자원 자체가 지금 사용 가능한가에 대한 확인
        if ((asset.getStatus() == 1) || (asset.getStatus() == 2))
            throw new IllegalArgumentException("asset not available");

        // 선착순 자원은 자동 승인
        List<Attendant> attendants = Attendant.create(users, reservation);

        Reservation reservation = createReservationRequestDto.toEntity(asset, applicant, attendants, 1);

        // TODO: toEntity 에 넣을지 이 내용을
        // reservation.addAttendants(attendants);
        reservationJpaRepository.save(reservation);

        return ReservationResponseDto.fromEntity(reservation, statusToString(1));
    }

    @Override
    @Transactional
    public ReservationResponseDto approveReservation(
            final Long reservationId, final ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한에 대한 확인

        Reservation reservation = getReservationById(reservationId);

        // TODO: respondent : userDetails.getUserId()
        reservation.approve(confirmReservationRequestDto.getReason()); // status approved

        reservationJpaRepository.save(reservation);
        return ReservationResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));
    }

    @Override
    @Transactional
    public ReservationResponseDto rejectReservation(
            final Long reservationId, final ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한에 대한 확인

        Reservation reservation = getReservationById(reservationId);

        // TODO: respondent : userDetails.getUserId()
        reservation.reject(confirmReservationRequestDto.getReason()); // status rejected

        reservationJpaRepository.save(reservation);

        return ReservationResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));
    }

    @Override
    @Transactional
    public ReservationResponseDto startUsingReservation(final Long reservationId, final Instant actualStartAt) {

        // 예약자 본인에 대한 확인

        // 실제 시작 시간 추가
        Reservation reservation = getReservationById(reservationId);

        reservation.start(); // status using

        reservationJpaRepository.save(reservation);
        return ReservationResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));
    }

    // 실제 종료 시간 추가
    @Override
    @Transactional
    public ReservationResponseDto endUsingReservation(final Long reservationId, final Instant actualEndAt) {

        // 예약자 본인에 대한 확인

        Reservation reservation = getReservationById(reservationId);

        reservation.end(); // status complete

        reservationJpaRepository.save(reservation);

        return ReservationResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));
    }

    // 사용 시간 30분 전인 경우 허용
    @Override
    @Transactional
    public ReservationResponseDto cancelReservation(final Long reservationId) {

        // 예약자 본인에 대한 확인

        Reservation reservation = getReservationById(reservationId);

        validateReservationCanceling(reservation);

        reservation.cancel(); // status complete

        reservationJpaRepository.save(reservation);

        return ReservationResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));
    }

    // 예약 정보 수정
    @Override
    @Transactional
    public ReservationResponseDto updateReservation(
            final Long reservationId, final UpdateReservationRequestDto updateReservationRequestDto) {

        // 예약자 본인에 대한 확인

        Reservation reservation = getReservationById(reservationId);

        List<Long> userIds = updateReservationRequestDto.getAttendantIds();
        // 예약자들 있는지 확인 -> user service에서

        reservation.update(updateReservationRequestDto.getDescription(), users);

        reservationJpaRepository.save(reservation);

        return ReservationResponseDto.fromEntity(reservation, statusToString(reservation.getStatus()));
    }

    // 자원 자체 (예외처리 포함)
    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationById(final Long id) {
        Reservation reservation = reservationJpaRepository
                .findById(id)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        return reservation;
    }

    // 자원 목록 (예외처리 포함)
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByAssetId(final Long assetId) {
        // assetId 유효한지 확인

        List<Reservation> reservations = reservationJpaRepository.findByAssetId(assetId);
        return reservations;
    }

    // api x 비즈니스 메서드
    private void validateReservationAvailability(final Long assetId, final Instant startAt, final Instant endAt) {
        if (!isReservationTimeAvailable(assetId, startAt, endAt))
            throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
    }

    private void validateReservationCanceling(Reservation reservation) {
        if (!isReservationCancelAvailable(reservation))
            // ddd -> 검증 / service 의 행동 결정(메시지 던짐)
            throw new ReservationException(ReservationErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
    }

    // 자원에 대한 예약 가능의 유무 -  비즈니스 책임이므로 command service로
    private boolean isReservationTimeAvailable(final Long assetId, final Instant startAt, final Instant endAt) {
        // assetId 유효 확인

        List<Reservation> reservations = getReservationsByAssetId(assetId);

        // 2-6인 경우 1-7, 2-4, 4-6, 3-4 모두 불가해야함
        for (Reservation reservation : reservations) {
            Instant existingStart = reservation.getStartAt();
            Instant existingEnd = reservation.getEndAt();

            // 둘 중 하나라도 달성되지 않으면 불가
            boolean overlaps = startAt.isBefore(existingEnd) && endAt.isAfter(existingStart);

            if (overlaps) {
                return false;
            }
        }
        return true;
    }

    private boolean isReservationCancelAvailable(final Reservation reservation) {
        Instant now = Instant.now();
        Instant deadline = reservation.getStartAt().minus(30, ChronoUnit.MINUTES);

        if (now.isBefore(deadline)) {
            return true; // 취소 가능
        }
        return false;
    }

    private String statusToString(Integer status) {
        if (status == 0) {
            return "PENDING";
        } else if (status == 1) {
            return "APPROVED";
        } else if (status == 2) {
            return "USING";
        } else if (status == 3) {
            return "REJECTED";
        } else if (status == 4) {
            return "CANCELED";
        } else {
            return "COMPLETED";
        }
    }
}
