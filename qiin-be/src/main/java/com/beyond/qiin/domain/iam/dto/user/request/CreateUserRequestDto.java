package com.beyond.qiin.domain.iam.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    // TODO: 사번 로직 생성으로 전환
    private String userNo;

    @NotBlank
    private String userName;

    @Email
    @NotBlank
    private String email;
}
