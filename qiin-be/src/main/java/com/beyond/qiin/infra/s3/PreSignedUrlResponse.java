package com.beyond.qiin.infra.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreSignedUrlResponse {
    private String uploadUrl; // S3에 업로드할 pre-signed URL
    private String fileUrl; // 최종적으로 DB에 저장할 S3 이미지 URL
}
