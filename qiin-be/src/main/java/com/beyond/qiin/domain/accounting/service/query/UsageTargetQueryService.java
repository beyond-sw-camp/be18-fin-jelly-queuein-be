package com.beyond.qiin.domain.accounting.service.query;

import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetStatusResponseDto;

public interface UsageTargetQueryService {

    UsageTargetStatusResponseDto getCurrentYearStatus();

    UsageTargetResponseDto getByYear(Integer year);
}
