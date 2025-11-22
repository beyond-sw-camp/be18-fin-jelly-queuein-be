package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 예약에 대한 상세 조회
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReservationDetailResponseDto {

    // 예약 id
    private final Long reservationId;

    // 자원명
    private final String assetName;

    // 승인자
    private final String respondentName;

    // 신청자 이름
    private final String applicantName;

    // 예약 상태
    private final String status;

    private final LocalDate date; // 날짜 표시용

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

    private final Long version;

    private final boolean isApproved;

    // 참여자들 - 해당 리스트에는 추가적인 역할이 없기 때문에 AttendantListResponseDto로 분리 x
    // 큰 차이 없으나 attendant(entity)가 종속되지 않도록 dto로 추가
    @Builder.Default
    private final List<AttendantResponseDto> attendants = new ArrayList<>();

    public static ReservationDetailResponseDto fromEntity(
            final Reservation reservation) {
        ReservationDetailResponseDto reservationDetailResponseDto = ReservationDetailResponseDto.builder()
                .reservationId(reservation.getId())
                .applicantName(reservation.getApplicant().getUserName())
                .assetName(reservation.getAsset().getName())
                .startAt(reservation.getStartAt())
                .endAt(reservation.getEndAt())
                .actualStartAt(reservation.getActualStartAt())
                .actualEndAt(reservation.getActualEndAt())
                .description(reservation.getDescription())
                .reason(reservation.getReason())
                .version(reservation.getVersion())
                .isApproved(reservation.isApproved())
                .attendants( // new ArrayList<>() 이므로 null 처리 생략
                        reservation.getAttendants().stream()
                                .map(attendant -> AttendantResponseDto.fromEntity(attendant))
                                .toList())
                .date(reservation.getStartAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate())
                .status(reservation.getStatus().name())
                .build();

        return reservationDetailResponseDto;
    }
}
