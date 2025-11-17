package com.beyond.qiin.domain.booking.dto.user_rev.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.entity.User;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Attendant extends BaseEntity {
    @ManyToOne
    @Builder.Default
    private User user;

    public static List<Attendant> create(List<User> users, Reservation reservation) {
        return users.stream()
                .map(user ->
                        Attendant.builder().reservation(reservation).user(user).build())
                .toList();
    }
}
