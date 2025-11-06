package com.beyond.qiin.booking.reservation.entity;

import com.beyond.qiin.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "reservation")
@AttributeOverride(name = "id", column = @Column(name = "reservation_id"))
@SQLRestriction("deleted_at = null")
public class Reservation extends BaseEntity {
  //신청자

  //승인자

  //자원

  @Column(name = "start_at", nullable = false)
  private Instant startAt;

  @Column(name = "end_at", nullable = false)
  private Instant endAt;

  @Column(name = "actual_start_at", nullable = true)
  private Instant actualStartAt;

  @Column(name = "actual_end_at", nullable = true)
  private Instant actualEndAt;

  @Column(name = "status", nullable = false)
  @Builder.Default
  private int status = 0;

  @Column(name = "description", length = 500, nullable = true)
  private String description;

  @Column(name = "version", nullable = false)
  @Builder.Default
  private int version = 0;

  @Column(name = "is_approved", nullable = false)
  @Builder.Default
  private boolean isApproved = false;

  @Column(name = "reason", nullable = true)
  private String reason;


}
