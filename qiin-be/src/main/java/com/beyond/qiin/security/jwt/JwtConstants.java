package com.beyond.qiin.security.jwt;

/**
 * JWT Claim Key 및 TokenType 관련 상수를 모아둔 클래스.
 * 문자열 오타를 방지하고, claim 키의 일관성을 유지하는 데 사용.
 */
public final class JwtConstants {

    private JwtConstants() {} // 인스턴스화 방지

    // ------------------------
    // Claim Keys
    // ------------------------
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_TOKEN_TYPE = "token_type";

    // ------------------------
    // Token Types
    // ------------------------
    public static final String ACCESS = "ACCESS";
    public static final String REFRESH = "REFRESH";
}
