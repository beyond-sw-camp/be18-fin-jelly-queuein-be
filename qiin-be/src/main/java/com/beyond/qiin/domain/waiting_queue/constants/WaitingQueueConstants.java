package com.beyond.qiin.domain.waiting_queue.constants;

public class WaitingQueueConstants {
    public static final long AUTO_ACTIVE_EXPIRE_TIME = 10 * 1000L; // 10초
    public static final long AUTO_WAIT_EXPIRE_TIME = 60 * 1000L; // 60초 (대기 시간 보장)
    public static final int ENTER_10_SECONDS = 3; // 10초당 m명 처리
    public static final String ACTIVE_KEY = "waiting:active";
    public static final String WAIT_KEY = "waiting:wait";
    public static final int MAX_ACTIVE_USERS = 5;
    public static final String REDIS_NAMESPACE = "hhplus:";
    //    public static final String ACTIVE_COUNT_KEY = "hhplus:waiting:active:*";
    public static final String WAITING_QUEUE_TOKEN = "WAITING_QUEUE_TOKEN";
}
