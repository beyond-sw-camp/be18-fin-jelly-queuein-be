package com.beyond.qiin.internal.master.service;

import com.beyond.qiin.internal.master.dto.request.RegisterMasterRequestDto;
import com.beyond.qiin.internal.master.dto.response.RegisterMasterResponseDto;

public interface MasterService {

    RegisterMasterResponseDto createMaster(final RegisterMasterRequestDto request);
}
