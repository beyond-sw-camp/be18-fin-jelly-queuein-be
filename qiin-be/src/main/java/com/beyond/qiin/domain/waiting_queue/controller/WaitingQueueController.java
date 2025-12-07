package com.beyond.qiin.domain.waiting_queue.controller;

import com.beyond.qiin.domain.waiting_queue.dto.WaitingQueueResponseDto;
import com.beyond.qiin.domain.waiting_queue.service.WaitingQueueCommandService;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.resolver.AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/waiting-queues")
public class WaitingQueueController {
    private final JwtTokenProvider jwtTokenProvider;

    private final WaitingQueueCommandService waitingQueueCommandService;

    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PostMapping
    public ResponseEntity<WaitingQueueResponseDto> enterWaitingQueue(@AccessToken final String accessToken) {
        final Long userId = jwtTokenProvider.getUserId(accessToken);

        WaitingQueueResponseDto waitingQueueResponseDto = waitingQueueCommandService.intoQueue(userId);
        return ResponseEntity.status(201).body(waitingQueueResponseDto);
    }

    // 프론트에서 폴링 용도(대기 진입 후 활성까지)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/status")
    public ResponseEntity<WaitingQueueResponseDto> getStatus(
            @AccessToken final String accessToken, @RequestParam final String token) {

        return ResponseEntity.ok(waitingQueueCommandService.checkStatus(token));
    }

    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PostMapping("/enter")
    public ResponseEntity<WaitingQueueResponseDto> enterService(
            @AccessToken final String accessToken, @RequestParam final String token) {
        final Long userId = jwtTokenProvider.getUserId(accessToken);

        // ACTIVE 상태인지 검증(입장 권한 부여), 입장 처리(토큰 소모, 입장 확정)
        WaitingQueueResponseDto dto = waitingQueueCommandService.enterService(userId, token);

        return ResponseEntity.ok(dto);
    }

    // 관리자의 강제 만료 처리 용도
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER')")
    @DeleteMapping
    public ResponseEntity<Void> expire(@AccessToken final String accessToken, @RequestParam final String token) {
        waitingQueueCommandService.forceExpireToken(token);
        return ResponseEntity.noContent().build();
    }
}
