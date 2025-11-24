package com.beyond.qiin.domain.booking.reservation.service.command;

import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.attendant.entity.Attendant;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.booking.reservation.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationException;
import com.beyond.qiin.domain.booking.reservation.reader.ReservationReader;
import com.beyond.qiin.domain.booking.reservation.writer.ReservationWriter;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final UserReader userReader;
    private final ReservationReader reservationReader;
    private final ReservationWriter reservationWriter;
    private final AssetCommandService assetCommandService;

    // TODO : 선착순, 승인 예약 중복 처리
    // TODO : entity 생성은 entity 안에서
    // 승인 예약
    @Override
    @Transactional
    public ReservationResponseDto applyReservation(
            final Long userId, final Long assetId, final CreateReservationRequestDto createReservationRequestDto) {

        Asset asset = assetCommandService.getAssetById(assetId);
        User applicant = userReader.findById(userId);
        userReader.validateAllExist(createReservationRequestDto.getAttendantIds());
        List<User> attendantUsers = userReader.findAllByIds(createReservationRequestDto.getAttendantIds());
        assetCommandService.isAvailable(assetId); // 자원 자체가 지금 사용 가능한가에 대한 확인

        Reservation reservation = createReservationRequestDto.toEntity(asset, applicant, ReservationStatus.PENDING);

        List<Attendant> attendants = attendantUsers.stream()
                .map(user -> Attendant.create(user, reservation))
                .collect(Collectors.toList());

        reservation.addAttendants(attendants);

        reservationWriter.save(reservation);

        return ReservationResponseDto.fromEntity(reservation);
    }

    // 선착순 예약
    @Override
    @Transactional
    public ReservationResponseDto instantConfirmReservation(
            final Long userId, final Long assetId, final CreateReservationRequestDto createReservationRequestDto) {

        Asset asset = assetCommandService.getAssetById(assetId);
        User applicant = userReader.findById(userId);
        userReader.validateAllExist(createReservationRequestDto.getAttendantIds()); // 참여자 목록의 사용자들이 모두 존재하는지에 대한 확인
        List<User> attendantUsers = userReader.findAllByIds(createReservationRequestDto.getAttendantIds());
        assetCommandService.isAvailable(assetId);
        // 해당 시간에 사용 가능한 자원인지 확인
        validateReservationAvailability(
                asset.getId(), createReservationRequestDto.getStartAt(), createReservationRequestDto.getEndAt());

        // 선착순 자원은 자동 승인
        Reservation reservation = createReservationRequestDto.toEntity(asset, applicant, ReservationStatus.APPROVED);
        reservation.setIsApproved(true); // 승인됨

        List<Attendant> attendants = attendantUsers.stream()
                .map(user -> Attendant.create(user, reservation))
                .collect(Collectors.toList());

        reservation.addAttendants(attendants);

        reservationWriter.save(reservation);

        return ReservationResponseDto.fromEntity(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto approveReservation(
            final Long userId,
            final Long reservationId,
            final ConfirmReservationRequestDto confirmReservationRequestDto) {

        User respondent = userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        Asset asset = reservation.getAsset();
        // 해당 시간에 사용 가능한 자원인지 확인
        validateReservationAvailability(asset.getId(), reservation.getStartAt(), reservation.getEndAt());

        reservation.approve(respondent, confirmReservationRequestDto.getReason()); // status approved
        reservationWriter.save(reservation);
        return ReservationResponseDto.fromEntity(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto rejectReservation(
            final Long userId,
            final Long reservationId,
            final ConfirmReservationRequestDto confirmReservationRequestDto) {

        User respondent = userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        reservation.reject(respondent, confirmReservationRequestDto.getReason()); // status rejected
        reservationWriter.save(reservation);
        return ReservationResponseDto.fromEntity(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto startUsingReservation(
            final Long userId, final Long reservationId, final Instant actualStartAt) {

        // 예약자 본인에 대한 확인
        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        reservation.start(); // status using, 실제 시작 시간 추가
        reservationWriter.save(reservation);
        return ReservationResponseDto.fromEntity(reservation);
    }

    // 실제 종료 시간 추가
    @Override
    @Transactional
    public ReservationResponseDto endUsingReservation(
            final Long userId, final Long reservationId, final Instant actualEndAt) {
        // 예약자 본인에 대한 확인
        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        reservation.end(); // status complete, 실제 종료 시간 추가
        reservationWriter.save(reservation);

        return ReservationResponseDto.fromEntity(reservation);
    }

    // 예약 취소
    @Override
    @Transactional
    public ReservationResponseDto cancelReservation(final Long userId, final Long reservationId) {

        // 예약자 본인에 대한 확인
        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        validateReservationCanceling(reservation); // 30분 전인 경우 허용
        reservation.cancel();

        reservationWriter.save(reservation);

        return ReservationResponseDto.fromEntity(reservation);
    }

    // 예약 정보 수정
    @Override
    @Transactional
    public ReservationResponseDto updateReservation(
            final Long userId,
            final Long reservationId,
            final UpdateReservationRequestDto updateReservationRequestDto) {

        // 예약자 본인에 대한 확인
        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);

        if (updateReservationRequestDto.getDescription() != null) {
            reservation.changeDescription(updateReservationRequestDto.getDescription());
        }

        if (updateReservationRequestDto.getStartAt() != null && updateReservationRequestDto.getEndAt() != null) {
            reservation.changeSchedule(
                    updateReservationRequestDto.getStartAt(), updateReservationRequestDto.getEndAt());
        }

        if (updateReservationRequestDto.getAttendantIds() != null
                && !updateReservationRequestDto.getAttendantIds().isEmpty()) {
            userReader.validateAllExist(updateReservationRequestDto.getAttendantIds());
            List<User> attendants = userReader.findAllByIds(updateReservationRequestDto.getAttendantIds());
            reservation.changeAttendants(attendants);
        }

        reservationWriter.save(reservation);

        return ReservationResponseDto.fromEntity(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(final Long userId, final Long reservationId) {
        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        if (reservation.getStatus() == ReservationStatus.USING) {
            throw new ReservationException(ReservationErrorCode.USING_RESERVATION_NOT_DELETED);
        }
        reservation.delete(userId);
        reservationWriter.save(reservation);
    }

    // api x 비즈니스 메서드
    private void validateReservationAvailability(final Long assetId, final Instant startAt, final Instant endAt) {
        if (!isReservationTimeAvailable(assetId, startAt, endAt))
            throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
    }

    private void validateReservationCanceling(final Reservation reservation) {
        if (!isReservationCancelAvailable(reservation))
            // ddd -> 검증 / service 의 행동 결정(메시지 던짐)
            throw new ReservationException(ReservationErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
    }

    // 자원에 대한 예약 가능의 유무 -  비즈니스 책임이므로 command service로
    private boolean isReservationTimeAvailable(final Long assetId, final Instant startAt, final Instant endAt) {
        // assetId 유효 확인

        List<Reservation> reservations = reservationReader.getReservationsByAssetId(assetId);

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

    private String statusToString(final Integer status) {
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
