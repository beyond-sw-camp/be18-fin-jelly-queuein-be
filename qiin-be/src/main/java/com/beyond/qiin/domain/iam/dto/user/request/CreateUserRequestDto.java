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

    //  @Pattern(
    //      regexp = "^010-?\\d{4}-?\\d{4}$",
    //      message = "연락처는 010-0000-0000 형식이어야 합니다."
    //  )
    private String phone;

    //  @Pattern(
    //      regexp = "^\\d{4}-\\d{2}-\\d{2}$",
    //      message = "생년월일은 yyyy-MM-dd 형식이어야 합니다."
    //  )
    private String birth;
}
