package com.beyond.qiin.domain.booking.support;

import com.beyond.qiin.domain.booking.entity.Attendant;
import com.beyond.qiin.domain.booking.repository.AttendantJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendantWriter {
    private final AttendantJpaRepository attendantJpaRepository;

    public void save(Attendant attendant) {
        attendantJpaRepository.save(attendant);
    }

    public void saveAll(List<Attendant> attendants) {
        attendantJpaRepository.saveAll(attendants);
    }
}
