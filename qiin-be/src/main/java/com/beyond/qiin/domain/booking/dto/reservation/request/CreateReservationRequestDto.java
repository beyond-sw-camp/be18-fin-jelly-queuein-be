package com.beyond.qiin.domain.booking.dto.reservation.request;

import com.beyond.qiin.domain.booking.dto.user_rev.entity.Attendant;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.entity.User;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CreateReservationRequestDto {

    // 신청자
    @NotNull
    private final String applicantName;

    // 자원명
    @NotNull
    private final String assetName;

    // 예약 시작 시간
    @NotNull
    private final Instant startAt;

    // 예약 종료 시간
    @NotNull
    private final Instant endAt;

    // 예약 설명
    @NotNull
    private final String description;

    // 버전
    @NotNull
    private final Long version;

    @NotNull
    private final String status;

    // 참여자들
    @NotNull
    @Builder.Default
    private List<Long> attendantIds = new ArrayList<>();

    public Reservation toEntity(
            final Asset asset, final User applicant, final List<Attendant> attendants, final Integer status) {

        Reservation reservation = Reservation.builder()
                .asset(asset)
                .applicant(applicant)
                .startAt(startAt)
                .endAt(endAt)
                .description(description)
                .version(version)
                .status(status)
                .attendants(attendants)
                .build();

        reservation.addAttendants(attendants);

        return reservation;
    }
}
