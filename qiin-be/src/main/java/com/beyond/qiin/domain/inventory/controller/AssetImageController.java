package com.beyond.qiin.domain.inventory.controller;

import com.beyond.qiin.infra.s3.PreSignedUrlResponse;
import com.beyond.qiin.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets/images")
@RequiredArgsConstructor
public class AssetImageController {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public PreSignedUrlResponse generateUploadUrl(
            @RequestParam String extension,
            @RequestParam String contentType
    ) {
        return s3Service.generateUploadUrl(extension, contentType);
    }
}
