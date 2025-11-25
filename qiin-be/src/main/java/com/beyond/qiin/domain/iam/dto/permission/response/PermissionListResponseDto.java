package com.beyond.qiin.domain.iam.dto.permission.response;

import com.beyond.qiin.domain.iam.entity.Permission;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionListResponseDto {
    private final List<PermissionResponseDto> permissions;

    public static PermissionListResponseDto from(final List<Permission> entities) {
        return PermissionListResponseDto.builder()
                .permissions(
                        entities.stream().map(PermissionResponseDto::fromEntity).toList())
                .build();
    }
}
