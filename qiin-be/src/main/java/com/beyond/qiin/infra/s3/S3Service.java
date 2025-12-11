package com.beyond.qiin.infra.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner presigner;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    // 1) 파일명 UUID 자동 생성
    private String generateFileName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    public PreSignedUrlResponse generateUploadUrl(String extension, String contentType) {

        // 2) UUID 기반 파일명 생성
        String filename = generateFileName(extension);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                                                         .bucket(bucket)
                                                         .key(filename)
                                                         .contentType(contentType)
                                                         .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                                                        .signatureDuration(Duration.ofMinutes(10))
                                                                        .putObjectRequest(objectRequest)
                                                                        .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        return new PreSignedUrlResponse(
                presigned.url().toString(),
                "https://" + bucket + ".s3.amazonaws.com/" + filename
        );
    }
}
