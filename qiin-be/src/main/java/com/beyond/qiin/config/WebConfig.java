package com.beyond.qiin.config;

import com.beyond.qiin.security.resolver.AccessTokenResolver;
import com.beyond.qiin.security.resolver.CurrentUserResolver;
import com.beyond.qiin.security.resolver.SseAccessTokenArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AccessTokenResolver accessTokenResolver;
    private final SseAccessTokenArgumentResolver sseAccessTokenArgumentResolver;
    private final CurrentUserResolver currentUserResolver;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessTokenResolver);
        resolvers.add(currentUserResolver);
        resolvers.add(sseAccessTokenArgumentResolver);
    }
}
