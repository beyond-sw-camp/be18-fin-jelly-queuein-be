package com.beyond.qiin.domain.booking.service.command;

import static com.beyond.qiin.domain.booking.constants.WaitingQueueConstants.AUTO_EXPIRED_TIME;
import static com.beyond.qiin.domain.booking.constants.WaitingQueueConstants.ENTER_10_SECONDS;

import com.beyond.qiin.common.annotation.DistributedLock;
import com.beyond.qiin.domain.booking.entity.WaitingQueue;
import com.beyond.qiin.domain.booking.enums.WaitingQueueStatus;
import com.beyond.qiin.domain.booking.repository.WaitingQueueRepository;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueCommandServiceImpl implements WaitingQueueCommandService {

    private final JwtTokenProvider jwtUtils; // 토큰 발급
    private final WaitingQueueRepository waitingQueueRepository; // 대기열, 할성 큐 관리

    // 사용자가 선착순 예약에 접근 시 : 토큰 활성화 여부를 체크해 토큰 대기열 정보 반환 - 상태 계산, 수정 동시에 불가
    @Transactional
    @DistributedLock(key = "'waitingQueueLock'")
    @Override
    public WaitingQueue intoQueue(User user) {
        log.info("[QUEUE-ENTER] userId={}", user.getId());
        // token 발급
        String token = UUID.randomUUID().toString();

        // 현재 활성 유저 수 확인
        long activeTokenCnt = waitingQueueRepository.getActiveCnt();

        // 활성화시킬 수 있는 수 계산
        long availableActiveTokenCnt = WaitingQueue.calculateActiveCnt(activeTokenCnt);

        log.info(
                "[QUEUE-STATUS] userId={}, activeCnt={}, availableCnt={}",
                user.getId(),
                activeTokenCnt,
                availableActiveTokenCnt);

        if (availableActiveTokenCnt > 0) { // 활성화시킬 수 있는 수가 남아있다면
            return intoActiveQueue(user, token); // 활성화 정보 반환(현재 자원 사용 가능)
        }
        return intoWaitingQueue(user, token); // 대기열 정보 반환(아직 자원 사용 불가)
    }

    @Override
    @Transactional
    public WaitingQueue intoActiveQueue(User user, String token) {
        // 활성 유저열에 추가
        waitingQueueRepository.saveActiveQueue(user, token);

        log.info("[QUEUE-ACTIVE] userId={}, token={}", user.getId(), token);
        // ttl 설정 - 일정 시간 동안의 예약 기능 사용
        waitingQueueRepository.setTimeOut(token, AUTO_EXPIRED_TIME, TimeUnit.MILLISECONDS);

        // 대기열에서 토큰 정보 제거
        waitingQueueRepository.deleteWaitingQueue(user, token);

        // 활성화 정보 반환(사용자에게 알려주는 용도)
        return WaitingQueue.builder()
                .userId(user.getId())
                .token(token)
                .status(WaitingQueueStatus.ACTIVE.getCode())
                .waitingNum(0L) // 활성 큐는 대기 순번 없음 → 0 또는 필요값
                .expireAt(Instant.now().plusMillis(AUTO_EXPIRED_TIME))
                .build();
    }

    @Override
    @Transactional
    public WaitingQueue intoWaitingQueue(User user, String token) {
        // 대기 순서 확인
        Long waitingNum = waitingQueueRepository.getWaitingNum(user, token);
        if (waitingNum == null) {
            log.info("[QUEUE-WAIT-NEW] userId={}, token={}", user.getId(), token);
            // 사용자에게 대기 순서 없음 == 대기열에 없는 유저
            // 대기열에 추가
            waitingQueueRepository.saveWaitingQueue(user, token);

            // 대기 순번 반환
            waitingNum = waitingQueueRepository.getWaitingNum(user, token);
        }
        // 대기 잔여  시간 계산 (10초당 활성 전환 수)
        long leftWaitingNum = (long) Math.ceil((double) (waitingNum - 1) / ENTER_10_SECONDS) * 10;

        log.info(
                "[QUEUE-WAIT] userId={}, token={}, waitingNum={}, left={}",
                user.getId(),
                token,
                waitingNum,
                leftWaitingNum);

        return WaitingQueue.builder()
                .userId(user.getId())
                .token(token)
                .status(WaitingQueueStatus.WAITING.getCode())
                .waitingNum(leftWaitingNum)
                .expireAt(Instant.now().plusMillis(AUTO_EXPIRED_TIME))
                .build();
    }

    // 대기열에서 활성 큐로 토큰을 전환하는 스케줄러용 로직
    // n초당 m명씩 active token으로 전환
    @Override
    @Transactional
    public void activateTokens() {

        // 대기열에서 순서대로 정해진 유저만큼 가져오기
        Set<String> waitingTokens = waitingQueueRepository.getWaitingTokens();
        log.info("[QUEUE-SCHEDULER] promoteTokens size={}", waitingTokens.size());
        // 대기열에서 가져온 만큼 삭제
        waitingQueueRepository.deleteWaitingTokens();

        // 활성화된 큐로 유저들 변경
        waitingQueueRepository.saveActiveQueues(waitingTokens);
        log.info("[QUEUE-SCHEDULER] promotion completed");
    }

    // active token 만료
    @Override
    @Transactional
    public void forceExpireToken(String token) {
        waitingQueueRepository.deleteExpiredToken(token);
    }
}
