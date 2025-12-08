package com.beyond.qiin.domain.booking.service.command;

import com.beyond.qiin.common.annotation.DistributedLock;
import com.beyond.qiin.domain.accounting.service.command.UsageHistoryCommandService;
import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Attendant;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.event.ReservationEventPublisher;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
import com.beyond.qiin.domain.booking.repository.AttendantJpaRepository;
import com.beyond.qiin.domain.booking.support.AttendantWriter;
import com.beyond.qiin.domain.booking.support.ReservationReader;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final UserReader userReader;
    private final ReservationReader reservationReader;
    private final ReservationWriter reservationWriter;
    private final AttendantWriter attendantWriter;
    private final AssetCommandService assetCommandService;
    private final ReservationEventPublisher reservationEventPublisher;
    private final AttendantJpaRepository attendantJpaRepository;
    private final UsageHistoryCommandService usageHistoryCommandService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

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

        Reservation reservation =
                Reservation.create(createReservationRequestDto, applicant, asset, ReservationStatus.PENDING);

        List<Attendant> attendants = attendantUsers.stream()
                .map(user -> Attendant.create(user, reservation))
                .collect(Collectors.toList());

        reservation.addAttendants(attendants);

        reservationWriter.save(reservation);

        attendantWriter.saveAll(attendants);

        reservationEventPublisher.publishCreated(reservation);

        return ReservationResponseDto.fromEntity(reservation);
    }

    // 선착순 예약 분산락 키 : 자원 id로만 두기 제한적
    @Override
    @Transactional
    @DistributedLock(
            key =
                    "'reservation:' + #assetId + ':' + #createReservationRequestDto.startAt + ':' + #createReservationRequestDto.endAt")
    public ReservationResponseDto instantConfirmReservation(
            final Long userId, final Long assetId, final CreateReservationRequestDto createReservationRequestDto) {

        Asset asset = assetCommandService.getAssetById(assetId);
        User applicant = userReader.findById(userId);
        userReader.validateAllExist(createReservationRequestDto.getAttendantIds()); // 참여자 목록의 사용자들이 모두 존재하는지에 대한 확인
        List<User> attendantUsers = userReader.findAllByIds(createReservationRequestDto.getAttendantIds());
        assetCommandService.isAvailable(assetId);
        // 해당 시간에 사용 가능한 자원인지 확인
        validateReservationAvailability(
                null, asset.getId(), createReservationRequestDto.getStartAt(), createReservationRequestDto.getEndAt());

        // 선착순 자원은 자동 승인
        Reservation reservation =
                Reservation.create(createReservationRequestDto, applicant, asset, ReservationStatus.APPROVED);
        reservation.setIsApproved(true); // 승인됨

        List<Attendant> attendants = attendantUsers.stream()
                .map(user -> Attendant.create(user, reservation))
                .collect(Collectors.toList());

        reservation.addAttendants(attendants);

        reservationWriter.save(reservation);

        attendantWriter.saveAll(attendants);
        reservationEventPublisher.publishCreated(reservation);

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
        validateReservationAvailability(
                reservation.getId(), asset.getId(), reservation.getStartAt(), reservation.getEndAt());

        reservation.approve(respondent, confirmReservationRequestDto.getReason()); // status approved
        reservationWriter.save(reservation);
        reservationEventPublisher.publishUpdated(reservation);
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

        reservationEventPublisher.publishUpdated(reservation);
        return ReservationResponseDto.fromEntity(reservation);
    }

    @Override
    @Transactional
    public ReservationResponseDto startUsingReservation(final Long userId, final Long reservationId) {

        // 예약자 본인에 대한 확인
        userReader.findById(userId);

        Reservation reservation = reservationReader.getReservationById(reservationId);
        // 지금 사용 불가한 자원이면 제외
        assetCommandService.isAvailable(reservation.getAsset().getId());

        // 시작 시간부터 사용 가능
        Instant now = Instant.now();
        if (now.isBefore(reservation.getStartAt())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_TIME_NOT_YET);
        }

        reservation.start(); // status using, 실제 시작 시간 추가
        reservationWriter.save(reservation);
        return ReservationResponseDto.fromEntity(reservation);
    }

    // 실제 종료 시간 추가
    @Override
    @Transactional
    public ReservationResponseDto endUsingReservation(final Long userId, final Long reservationId) {
        // 예약자 본인에 대한 확인
        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        reservation.end(); // status complete, 실제 종료 시간 추가
        reservationWriter.save(reservation);

        Asset asset = reservation.getAsset();

        usageHistoryCommandService.createUsageHistory(asset, reservation);

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

        // 수정 시 참여자들을 무조건 받는 구조 : id 없는 경우 -> 빈 배열일 때도 이전 추가된 참여들을 위해 삭제해야함
        // 기존 참여자들 삭제
        for (Attendant a : new ArrayList<>(reservation.getAttendants())) {
            reservation.removeAttendant(a); // 양방향 끊기
            attendantJpaRepository.delete(a); // DB에서 삭제
        }

        //
        if (!updateReservationRequestDto.getAttendantIds().isEmpty()) {
            // 추가할 참여자들에 대해 검증
            userReader.validateAllExist(updateReservationRequestDto.getAttendantIds());
            List<User> newAttendants = userReader.findAllByIds(updateReservationRequestDto.getAttendantIds());

            // 예약의 참여자들 변경
            List<Attendant> attendants = reservation.changeAttendants(newAttendants);
            attendantWriter.saveAll(attendants);
        }

        reservationWriter.save(reservation);

        return ReservationResponseDto.fromEntity(reservation);
    }

    // 예약에 대해  soft delete
    @Override
    @Transactional
    public void softDeleteReservation(final Long userId, final Long reservationId) {
        userReader.findById(userId);
        Reservation reservation = reservationReader.getReservationById(reservationId);
        if (reservation.getStatus() == ReservationStatus.USING) {
            throw new ReservationException(ReservationErrorCode.USING_RESERVATION_NOT_DELETED);
        }
        reservation.softDeleteAll(userId); // 예약, 참여자 둘다 soft delete 처리
        reservationWriter.save(reservation);
    }

    // 자원 상태 변경 시 예약 상태 변경
    @Override
    @Transactional
    public void updateReservationsForAsset(final Long assetId, final Integer assetStatus) {
        // 1 = UNAVAILABLE, 2 = MAINTENANCE
        if (assetStatus != 1 && assetStatus != 2) return;
        if (assetStatus == null) return;
        // pending, approved, using 대상(0, 1, 2)
        List<Reservation> reservations = reservationWriter.findFutureUsableReservations(assetId);

        for (Reservation reservation : reservations) {
            reservation.markUnavailable("자원 사용 불가 상태에 따른 자동 취소");
        }
    }

    // 하드 딜리트
    public void hardDeleteReservation(final Long reservationId) {
        reservationWriter.hardDelete(reservationId);
    }

    // api x 비즈니스 메서드
    private void validateReservationAvailability(
            final Long reservationId, final Long assetId, final Instant startAt, final Instant endAt) {
        if (!isReservationTimeAvailable(reservationId, assetId, startAt, endAt))
            throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
    }

    private void validateReservationCanceling(final Reservation reservation) {
        if (!isReservationCancelAvailable(reservation))
            // ddd -> 검증 / service 의 행동 결정(메시지 던짐)
            throw new ReservationException(ReservationErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
    }

    // test 가능하도록 package private 허용
    // 자원에 대한 예약 가능의 유무 -  비즈니스 책임이므로 command service로
    boolean isReservationTimeAvailable(
            final Long reservationId, final Long assetId, final Instant startAt, final Instant endAt) {

        List<Reservation> reservations = reservationReader.getActiveReservationsByAssetId(assetId);

        for (Reservation reservation : reservations) {

            if (reservationId != null) { // 생성시는 null
                if (reservation.getId().equals(reservationId)) {
                    continue;
                }
            }

            Instant existingStart = reservation.getStartAt();
            Instant existingEnd = reservation.getEndAt();

            // 딱 맞닿는 경우는 허용
            if (startAt.equals(existingEnd) || endAt.equals(existingStart)) {
                continue;
            }

            // 겹침 체크
            boolean overlaps = startAt.isBefore(existingEnd) && endAt.isAfter(existingStart);

            if (overlaps) return false;
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
}
