package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetStatusResponseDto;
import com.beyond.qiin.domain.accounting.entity.UsageTarget;
import com.beyond.qiin.domain.accounting.exception.UsageTargetException;
import com.beyond.qiin.domain.accounting.repository.UsageTargetJpaRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageTargetQueryServiceImpl implements UsageTargetQueryService {

    private final UsageTargetJpaRepository usageTargetJpaRepository;

    @Override
    public UsageTargetStatusResponseDto getCurrentYearStatus() {
        int year = LocalDate.now().getYear();

        return usageTargetJpaRepository
                .findByYear(year)
                .map(UsageTargetStatusResponseDto::exists)
                .orElse(UsageTargetStatusResponseDto.notExists(year));
    }

    @Override
    public UsageTargetResponseDto getByYear(Integer year) {

        UsageTarget entity = usageTargetJpaRepository.findByYear(year).orElseThrow(UsageTargetException::notFound);

        return UsageTargetResponseDto.builder()
                .id(entity.getId())
                .year(entity.getYear())
                .targetRate(entity.getTargetRate())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt().toString())
                .build();
    }
}
