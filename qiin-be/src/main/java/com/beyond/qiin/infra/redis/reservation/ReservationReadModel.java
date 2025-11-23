package com.beyond.qiin.infra.redis.reservation;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
// 조회 시 필요 데이터만 넣는 용

@Getter
@NoArgsConstructor // redis 에서 필요
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash("reservation")
public class ReservationReadModel {
    @Id
    private Long id;

    private String assetName;
    private String applicantName;
    private String respondentName;
    private boolean isApproved; // def null
    private int statusCode; // TODO : string / 비즈니스 표현 아니므로 변경성 낮은 숫자 활용
    private String description;
    private String reason;
    private long startAt;
    private long endAt;
    private Long actualStartAt; // nullable
    private Long actualEndAt; // nullable
    private int attendantCount; // 상세 조회시에만 필요 -> jpa로 가져옴
}
