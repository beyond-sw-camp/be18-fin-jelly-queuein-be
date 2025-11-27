package com.beyond.qiin.domain.iam.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateUserRequestDto {

    // TODO: 부서 엔티티 추가 후 검증 추가
    private Long dptId;

    @NotNull
    private LocalDate hireDate;

    @NotBlank
    private String userName;

    @Email
    @NotBlank
    private String email;
}
