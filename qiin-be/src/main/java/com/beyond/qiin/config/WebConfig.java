package com.beyond.qiin.config;

import com.beyond.qiin.security.resolver.ArgumentResolver;
import com.beyond.qiin.security.resolver.CurrentUserIdResolver;
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

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(argumentResolver);
        resolvers.add(currentUserIdResolver);
    }
}
