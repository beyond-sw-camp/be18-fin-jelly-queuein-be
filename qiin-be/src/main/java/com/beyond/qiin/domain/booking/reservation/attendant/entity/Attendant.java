package com.beyond.qiin.domain.booking.reservation.attendant.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.iam.entity.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(
    name = "reservation",
    indexes = {
        @Index(name = "idx_reservation_applicant_id", columnList = "applicant_id"),
        @Index(name = "idx_reservation_respondent_id", columnList = "respondent_id"),
        @Index(name = "idx_reservation_asset_id", columnList = "asset_id")
    })
@AttributeOverride(name = "id", column = @Column(name = "reservation_id"))
@SQLRestriction("deleted_at is null")
public class Attendant extends BaseEntity {

  @Column(name = "reservation_id", nullable = false)
  private Long reservationId;

  @ManyToOne
  @JoinColumn(name = "reservation_id",
      insertable = false, updatable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Reservation reservation;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id",
      insertable = false, updatable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private User user;


  public void setReservation(Reservation reservation) {
    this.reservation = reservation;
    this.reservationId = reservation.getId();
  }

  public void setUser(User user) {
    this.user = user;
    this.userId = user.getId();
  }

}
