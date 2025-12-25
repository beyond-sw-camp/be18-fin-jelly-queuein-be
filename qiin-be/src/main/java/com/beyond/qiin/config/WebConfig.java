package com.beyond.qiin.config;

import com.beyond.qiin.security.resolver.ArgumentResolver;
import com.beyond.qiin.security.resolver.CurrentUserIdResolver;
import com.beyond.qiin.security.resolver.CurrentUserRoleResolver;
import com.beyond.qiin.security.resolver.SseAccessTokenArgumentResolver;
import com.beyond.qiin.security.resolver.CurrentUserResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ArgumentResolver argumentResolver;
    private final CurrentUserIdResolver currentUserIdResolver;
    private final CurrentUserRoleResolver currentUserRoleResolver;
    private final SseAccessTokenArgumentResolver sseAccessTokenArgumentResolver;
    private final CurrentUserResolver currentUserResolver;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(argumentResolver);
        resolvers.add(currentUserResolver);
        resolvers.add(currentUserIdResolver);
        resolvers.add(currentUserRoleResolver);
        resolvers.add(sseAccessTokenArgumentResolver);
    }
}
