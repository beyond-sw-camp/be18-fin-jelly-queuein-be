package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.dto.usage_target.request.UsageTargetCreateRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;
import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import com.beyond.qiin.domain.accounting.exception.UsageTargetException;
import com.beyond.qiin.domain.accounting.repository.UsageTargetJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageTargetCommandServiceImpl implements UsageTargetCommandService {

    private final UsageTargetJpaRepository usageTargetJpaRepository;

    @Override
    public UsageTargetResponseDto createTarget(final UsageTargetCreateRequestDto request) {

        final int year = LocalDate.now().getYear();

        // 이미 올해 목표가 존재할 경우
        if (usageTargetJpaRepository.existsByYear(year)) {
            throw UsageTargetException.alreadyExists();
        }

        // 로그인 기능 완성 전 임시 값
        final Long userId = 1L;

        UsageTarget entity = UsageTarget.create(year, BigDecimal.valueOf(request.getTargetRate()), userId);

        UsageTarget saved = usageTargetJpaRepository.save(entity);

        return UsageTargetResponseDto.builder()
                .id(saved.getId())
                .year(saved.getYear())
                .targetRate(saved.getTargetRate())
                .createdBy(saved.getCreatedBy())
                .createdAt(saved.getCreatedAt().toString())
                .build();
    }
}
