package com.beyond.qiin.security.jwt;

/**
 * JWT Claim Key 및 TokenType 관련 상수를 모아둔 클래스.
 * 문자열 오타를 방지하고, claim 키의 일관성을 유지하는 데 사용.
 */
public final class JwtConstants {

    private JwtConstants() {} // 인스턴스화 방지

    // ------------------------
    // Header Keys
    // ------------------------
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length(); // 7 대신 사용

    // ------------------------
    // Claim Keys
    // ------------------------
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_TOKEN_TYPE = "token_type";
}
