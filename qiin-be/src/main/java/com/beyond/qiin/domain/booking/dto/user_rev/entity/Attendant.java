package com.beyond.qiin.domain.booking.dto.user_rev.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.entity.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_rev")
@AttributeOverride(name = "id", column = @Column(name = "user_rev_id"))
@SQLRestriction("deleted_at = null")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Attendant extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reservation reservation;

    public Attendant(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public static Attendant create(User user) {
        return Attendant.builder().user(user).build();
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        this.reservationId = reservation.getId();
    }
}
