package com.beyond.qiin.domain.accounting.service.command;

import com.beyond.qiin.domain.accounting.dto.usage_target.request.UsageTargetCreateRequestDto;
import com.beyond.qiin.domain.accounting.dto.usage_target.response.UsageTargetResponseDto;

public interface UsageTargetCommandService {

    UsageTargetResponseDto createTarget(UsageTargetCreateRequestDto request);
}
