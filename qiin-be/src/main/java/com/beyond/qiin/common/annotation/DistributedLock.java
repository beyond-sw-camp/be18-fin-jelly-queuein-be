package com.beyond.qiin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD) // method 실행 시 적용
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 정보 유지 시간 = runtime(framework이 어노테이션을 읽고 실행하는 시점)
public @interface DistributedLock {
    String key(); // lock 식별자

    TimeUnit timeUnit() default TimeUnit.SECONDS; // 락 시간 단위

    long waitTime() default 5L; // 락 대기 시간 5초

    long leaseTime() default 3L; // 락 임대 시간 3초
}
