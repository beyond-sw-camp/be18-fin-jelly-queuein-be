package com.beyond.qiin.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionAop {

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 부모 transaction 유무와 상관없이 별도 transaction으로 실행
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed(); // aop target method 실제 실행
    }
}
