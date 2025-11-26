package com.beyond.qiin.common.aop;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.beyond.qiin.common.annotation.DistributedLock;
import com.beyond.qiin.common.util.CustomSpringELParser;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;

    private final LockTransactionAop transactionAop;

    @Around("@annotation(com.beyond.qiin.common.annotation.DistributedLock)") // pointcut
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable { // aspect 대상 메서드
        // advice
        // aop 가 감싼 메서드의 시그니처를 가져옴 - 실행하는 메서드에 대한 정보
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 실제 실행될 메서드 객체를 가져옴
        Method method = signature.getMethod();

        // 대상 메서드 어노테이션에 적은 값 가져옴
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 키 생성
        String key = REDISSON_LOCK_PREFIX
                + CustomSpringELParser.getDynamicValue(
                        signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        // 키 식별자의 락 생성
        RLock rLock = redissonClient.getLock(key);

        log.info("[DISTRIBUTED-LOCK-AOP] Executing lock logic. method={}, key={}", method.getName(), key);

        try {
            // lock 접근 시도
            boolean available =
                    rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            log.info("[DISTRIBUTED-LOCK-AOP] method={} key={} -> lock acquired? {}", method.getName(), key, available);
            if (!available) {
                return false;
            }

            // proceed 함수 - 해당 transaction과 별도 transaction으로 실행(join point 실행 후 lock 해제 보장 용도)
            return transactionAop.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally { // 비즈니스 로직(함수 실행)에서 오류 나더라도 lock 해제 보장용
            try {
                rLock.unlock();
                log.info("[DISTRIBUTED-LOCK-AOP] method={} key={} -> UNLOCKED", method.getName(), key);
            } catch (IllegalMonitorStateException e) {
                log.info(
                        "Redisson Lock Already UnLocked {} {}",
                        kv("serviceName", method.getName()), // key, value 형태 로그 -> els, kafka
                        kv("key", key));
            }
        }
    }
}
