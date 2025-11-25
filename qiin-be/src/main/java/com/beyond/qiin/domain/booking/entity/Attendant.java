package com.beyond.qiin.domain.booking.entity;

import com.beyond.qiin.common.BaseEntity;
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
// @RedisHash("user") //redis hash 용

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        name = "attendant",
        indexes = {
            @Index(name = "idx_attendant_reservation_id", columnList = "reservation_id"),
            @Index(name = "idx_attendant_user_id", columnList = "user_id")
        })
@AttributeOverride(name = "id", column = @Column(name = "attendant_id"))
@SQLRestriction("deleted_at is null")
public class Attendant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Attendant create(User user, Reservation reservation) {
        return Attendant.builder().user(user).reservation(reservation).build();
    }
}
