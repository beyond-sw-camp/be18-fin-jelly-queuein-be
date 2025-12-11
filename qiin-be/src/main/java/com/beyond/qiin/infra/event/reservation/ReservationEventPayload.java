package com.beyond.qiin.infra.event.reservation;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationEventPayload {
    private String eventType; // 모든 서비스에서 enum을 맞추는 것보다 string으로 주는게 유연
    private Long reservationId;
    private Long assetId;
    private Long applicantId;
    private Long respondentId;

    private String startAt;
    private String endAt;

    private String status;

    @Builder.Default
    private List<Long> attendantUserIds = new ArrayList<>();

    public static ReservationEventPayload fromCreated(Reservation reservation, List<Long> attendants) {

        return ReservationEventPayload.builder()
                .eventType(ReservationEventType.CREATED.name())
                .reservationId(reservation.getId())
                .assetId(reservation.getAsset().getId())
                .applicantId(reservation.getApplicant().getId())
                .respondentId(null) // 생성 시 없음
                .startAt(reservation.getStartAt().toString())
                .endAt(reservation.getEndAt().toString())
                .status(reservation.getStatus().name())
                .attendantUserIds(attendants)
                .build();
    }

    public static ReservationEventPayload from(Reservation reservation, List<Long> attendants) {

        return ReservationEventPayload.builder()
                .eventType(resolveEventType(reservation))
                .reservationId(reservation.getId())
                .assetId(reservation.getAsset().getId())
                .applicantId(reservation.getApplicant().getId())
                .respondentId(
                        reservation.getRespondent() != null
                                ? reservation.getRespondent().getId()
                                : null)
                .startAt(
                        reservation.getStartAt() != null
                                ? reservation.getStartAt().toString()
                                : null)
                .endAt(reservation.getEndAt() != null ? reservation.getEndAt().toString() : null)
                .status(reservation.getStatus().name())
                .attendantUserIds(attendants)
                .build();
    }

    private static String resolveEventType(Reservation reservation) {

        return switch (reservation.getStatus()) {
            case PENDING -> ReservationEventType.CREATED.name();
            case APPROVED -> ReservationEventType.APPROVED.name();
            case REJECTED -> ReservationEventType.REJECTED.name();
            case UNAVAILABLE -> ReservationEventType.UNAVAILABLE.name();
            default -> throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        };
    }
}
