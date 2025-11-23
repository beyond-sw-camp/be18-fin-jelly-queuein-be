package com.beyond.qiin.infra.redis.reservation;

import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationRedisService {
  private final RedisTemplate<String, Object> redisTemplate;

  public void save(Reservation reservation) {
      String key = "reservation:" + reservation.getId();
      redisTemplate.opsForValue().set(key, ReservationResponseDto.fromEntity(reservation));
  }

  public void delete(Long id) {
      redisTemplate.delete("reservation:" + id);
  }
}
