package com.beyond.qiin.domain.booking.dto.reservation.request;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.inventory.entity.Asset;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CreateReservationRequestDto {

    // 신청자
    @NotNull
    private Long applicantId;

    // 예약 시작 시간
    @NotNull
    private Instant startAt;

    // 예약 종료 시간
    @NotNull
    private Instant endAt;

    // 예약 설명
    private String description;

    // 버전 : 생성 시 x

    // 참여자들
    @NotNull
    @Builder.Default
    private List<Long> attendantIds = new ArrayList<>();

    public Reservation toEntity(
            final Asset asset,
            final User applicant,
            // final List<Attendant> attendants,
            final ReservationStatus reservationStatus) {

        Reservation reservation = Reservation.builder()
                .asset(asset)
                .applicant(applicant)
                .startAt(startAt)
                .endAt(endAt)
                .description(description)
                .status(reservationStatus.getCode())
                .reservationStatus(reservationStatus)
                .build();

        // reservation.addAttendants(attendants);

        return reservation;
    }
}
