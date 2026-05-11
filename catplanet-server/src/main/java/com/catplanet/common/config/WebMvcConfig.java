package com.catplanet.common.config;

import com.catplanet.common.interceptor.AuthInterceptor;
import com.catplanet.common.interceptor.FamilyInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final FamilyInterceptor familyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");

        registry.addInterceptor(familyInterceptor)
                .addPathPatterns("/api/cats/**", "/api/records/**", "/api/timeline/**");
    }
}
