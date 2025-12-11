package com.beyond.qiin.security.constants;

public final class SecurityWhitelist {

    // 내부 시스템 호출용 API
    public static final String[] INTERNAL = {"/internal/**"};

    // 로그인 / 리프레시 등 인증 없이 접근 가능한 API
    public static final String[] AUTH = {"/api/v1/auth/**"};

    public static final String[] SSE = {"/api/v1/sse/**"};

    // 인스턴스화 방지
    private SecurityWhitelist() {}
}
