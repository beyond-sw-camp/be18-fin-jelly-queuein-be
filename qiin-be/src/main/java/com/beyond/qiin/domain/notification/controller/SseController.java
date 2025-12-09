package com.beyond.qiin.domain.notification.controller;

import com.beyond.qiin.domain.notification.service.SseService;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.resolver.AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SseController {
    private final JwtTokenProvider jwtTokenProvider;
    private final SseService sseService;

    // SSE 구독
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AccessToken final String accessToken) {

        final Long userId = jwtTokenProvider.getUserId(accessToken);

        return sseService.connect(userId);
    }

    // SSE 구독 해제
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER', 'GENERAL')")
    @DeleteMapping("/unsubscribe")
    public void unsubscribe(@AccessToken final String accessToken) {
        final Long userId = jwtTokenProvider.getUserId(accessToken);

        sseService.disconnect(userId);
    }
}
