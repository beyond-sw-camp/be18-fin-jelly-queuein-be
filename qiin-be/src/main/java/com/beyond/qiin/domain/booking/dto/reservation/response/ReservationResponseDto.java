package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ReservationResponseDto {

    // 예약 id
    private final Long reservationId;

    // 신청자 이름
    private final String applicantName;

    // 자원 이름
    private final String assetName;

    // 예약 시작 시간
    private final Instant startAt;

    // 예약 종료 시간
    private final Instant endAt;

    // 실제 사용 시간
    private final Instant actualStartAt;

    // 실제 종료 시간
    private final Instant actualEndAt;

    private final String description;

    private final String reason;

    // 버전
    private final Long version;

    // 승인 여부
    private final Boolean isApproved;

    // 예약 상태 (문자열 or enum)
    private final String status;

    // 생성 정보
    private final Instant createdAt;
    private final Long createdBy;

    // 수정 정보
    private final Instant updatedAt;
    private final Long updatedBy;

    // 삭제 정보
    private final Instant deletedAt;
    private final Long deletedBy;

    // 참여자 목록
    @Builder.Default
    private final List<AttendantResponseDto> attendants = new ArrayList<>();

    public static ReservationResponseDto fromEntity(final Reservation reservation, final String status) {

        return ReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .assetName(reservation.getAsset().getName())
                .applicantName(reservation.getApplicant().getUserName())
                .startAt(reservation.getStartAt())
                .endAt(reservation.getEndAt())
                .actualStartAt(reservation.getActualStartAt())
                .actualEndAt(reservation.getActualEndAt())
                .description(reservation.getDescription())
                .reason(reservation.getReason())
                .version(reservation.getVersion())
                .isApproved(reservation.isApproved())
                .status(status)
                .attendants(reservation.getAttendants().stream()
                        .map(attendant -> AttendantResponseDto.fromEntity(attendant))
                        .toList())
                .createdAt(reservation.getCreatedAt())
                .createdBy(reservation.getCreatedBy())
                .updatedAt(reservation.getUpdatedAt())
                .updatedBy(reservation.getUpdatedBy())
                .deletedAt(reservation.getDeletedAt())
                .deletedBy(reservation.getDeletedBy())
                .build();
    }
}
