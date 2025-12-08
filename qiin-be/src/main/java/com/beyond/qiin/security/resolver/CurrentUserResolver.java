package com.beyond.qiin.security.resolver;

import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(CurrentUserContext.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw AuthException.unauthorized();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails user)) {
            throw AuthException.unauthorized();
        }

        // 요청 정보 추출
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String ip = request != null ? request.getRemoteAddr() : null;
        String userAgent = request != null ? request.getHeader("User-Agent") : null;

        // 권한 분리: ROLE_* 와 PERMISSION_*
        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String roleName = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .orElse(null);

        List<String> permissions =
                authorities.stream().filter(a -> !a.startsWith("ROLE_")).collect(Collectors.toList());

        // CurrentUserContext 생성 후 반환
        return CurrentUserContext.of(user.getUserId(), user.getEmail(), roleName, permissions, ip, userAgent);
    }
}
