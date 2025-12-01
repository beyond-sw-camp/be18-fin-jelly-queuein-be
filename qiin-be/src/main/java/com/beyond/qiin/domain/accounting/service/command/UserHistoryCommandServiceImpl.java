package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.entity.UsageHistory;
import com.beyond.qiin.domain.accounting.entity.UserHistory;
import com.beyond.qiin.domain.accounting.repository.UserHistoryJpaRepository;
import com.beyond.qiin.domain.booking.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserHistoryCommandServiceImpl implements UserHistoryCommandService {

    private final UserHistoryJpaRepository userHistoryJpaRepository;

    @Override
    @Transactional
    public void createUserHistories(Reservation reservation, UsageHistory usageHistory) {

        if (reservation.getAttendants() == null) {
            return;
        }

        reservation.getAttendants().forEach(attendant -> {
            UserHistory history = UserHistory.builder()
                    .user(attendant.getUser())
                    .usageHistory(usageHistory) // FK 연결
                    .build();

            userHistoryJpaRepository.save(history);
        });
    }
}
