package com.beyond.qiin.domain.iam.dto.user.request;

import java.time.Instant;
import lombok.Getter;

@Getter
public class UpdateUserByAdminRequestDto {

    private Long dptId;
    private Instant retireDate;
}
