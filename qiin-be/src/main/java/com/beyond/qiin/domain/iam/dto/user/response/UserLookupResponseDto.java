package com.beyond.qiin.domain.iam.dto.user.response;

import com.beyond.qiin.domain.iam.dto.user.response.raw.RawUserLookupDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserLookupResponseDto {

    private final Long userId;
    private final String userName;
    private final String email;

    public static UserLookupResponseDto from(final RawUserLookupDto raw) {
        return UserLookupResponseDto.builder()
                .userId(raw.getUserId())
                .userName(raw.getUserName())
                .email(raw.getEmail())
                .build();
    }
}
