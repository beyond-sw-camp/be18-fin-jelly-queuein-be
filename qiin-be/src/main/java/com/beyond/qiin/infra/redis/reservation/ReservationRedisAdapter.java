package com.beyond.qiin.infra.redis.reservation;

import com.beyond.qiin.domain.booking.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationRedisAdapter {

    private final ReservationRedisRepository redisRepository;

    public void save(Reservation reservation) {

        ReservationReadModel model = ReservationReadModel.builder()
                .id(reservation.getId())
                .assetName(reservation.getAsset().getName())
                .applicantName(reservation.getApplicant().getUserName())
                .respondentName(
                        reservation.getRespondent() != null
                                ? reservation.getRespondent().getUserName()
                                : null)
                .isApproved(reservation.getIsApproved())
                .statusCode(reservation.getStatus().getCode())
                .startAt(reservation.getStartAt().getEpochSecond())
                .endAt(reservation.getEndAt().getEpochSecond())
                .attendantCount(reservation.getAttendants().size())
                .reason(reservation.getReason())
                .description(reservation.getDescription())
                .build();

        redisRepository.save(model);
    }

    public void delete(Long id) {
        redisRepository.deleteById(id);
    }
}
