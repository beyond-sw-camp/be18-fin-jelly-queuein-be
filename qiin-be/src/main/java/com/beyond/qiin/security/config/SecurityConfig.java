package com.beyond.qiin.security.config;

import com.beyond.qiin.security.constants.SecurityWhitelist;
import com.beyond.qiin.security.jwt.JwtFilter;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.jwt.RedisTokenRepository;
import com.beyond.qiin.security.login.JsonLoginFailureHandler;
import com.beyond.qiin.security.login.JsonLoginFilter;
import com.beyond.qiin.security.login.JsonLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final JwtFilter jwtFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenRepository redisTokenRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(SecurityWhitelist.AUTH)
                        .permitAll()
                        .requestMatchers(SecurityWhitelist.INTERNAL)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler));

        // AuthenticationManager 활용
        final AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

        // 필터 생성
        final JsonLoginFilter loginFilter = new JsonLoginFilter(authenticationManager);
        loginFilter.setFilterProcessesUrl("/api/v1/auth/login");
        loginFilter.setAuthenticationSuccessHandler(
                new JsonLoginSuccessHandler(jwtTokenProvider, redisTokenRepository));
        loginFilter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());

        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, JsonLoginFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
