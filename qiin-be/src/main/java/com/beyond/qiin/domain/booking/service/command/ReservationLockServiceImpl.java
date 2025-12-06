package com.beyond.qiin.domain.booking.service.command;

import com.beyond.qiin.common.annotation.DistributedLock;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.entity.Attendant;
import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.event.ReservationEventPublisher;
import com.beyond.qiin.domain.booking.support.AttendantWriter;
import com.beyond.qiin.domain.booking.support.ReservationValidator;
import com.beyond.qiin.domain.booking.support.ReservationWriter;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationLockServiceImpl implements ReservationLockService {

    private final AssetCommandService assetCommandService;
    private final UserReader userReader;
    private final ReservationWriter reservationWriter;
    private final AttendantWriter attendantWriter;
    private final ReservationEventPublisher reservationEventPublisher;
    private final ReservationValidator reservationValidator;

    // 선착순 예약 분산락 키 : 자원 id로만 두기 제한적
    @Override
    @Transactional
    @DistributedLock(
            key =
                    "'reservation:' + #assetId + ':' + #createReservationRequestDto.startAt + ':' + #createReservationRequestDto.endAt")
    public ReservationResponseDto reserveReservationWithLock(
            final Long userId, final Long assetId, final CreateReservationRequestDto createReservationRequestDto) {

        Asset asset = assetCommandService.getAssetById(assetId);
        User applicant = userReader.findById(userId);
        userReader.validateAllExist(createReservationRequestDto.getAttendantIds()); // 참여자 목록의 사용자들이 모두 존재하는지에 대한 확인
        List<User> attendantUsers = userReader.findAllByIds(createReservationRequestDto.getAttendantIds());
        assetCommandService.isAvailable(assetId);
        // 해당 시간에 사용 가능한 자원인지 확인
        reservationValidator.validateReservationAvailability(
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
}
