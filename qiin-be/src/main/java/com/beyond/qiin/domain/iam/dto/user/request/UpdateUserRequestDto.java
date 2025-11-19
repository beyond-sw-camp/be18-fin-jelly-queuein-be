package com.beyond.qiin.domain.iam.dto.user.request;

import jakarta.validation.constraints.Email;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserRequestDto {

    private Long dptId;

    private String userName;

    @Email
    private String email;

    private Instant retireDate;
}
