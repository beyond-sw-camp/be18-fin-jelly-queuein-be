package com.beyond.qiin.security.resolver;

import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.security.CustomUserDetails;
import com.beyond.qiin.security.util.PermissionUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
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
    public boolean supportsParameter(MethodParameter parameter) {
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

        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // ROLE (ROLE_MASTER → MASTER)
        String roleName = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse(null);

        // Permissions (ROLE 제외)
        List<String> permissions =
                authorities.stream().filter(a -> !a.startsWith("ROLE_")).toList();

        // Domain Group
        Map<String, List<String>> permissionGroups = PermissionUtils.groupPermissions(permissions);

        // IP / UserAgent
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        String ip = resolveClientIp(req);
        String ua = req != null ? req.getHeader("User-Agent") : null;

        return CurrentUserContext.of(user.getUserId(), user.getEmail(), roleName, permissionGroups, ip, ua);
    }

    private String resolveClientIp(final HttpServletRequest request) {
        if (request == null) return null;
        String xf = request.getHeader("X-Forwarded-For");
        return (xf != null) ? xf.split(",")[0].trim() : request.getRemoteAddr();
    }
}
