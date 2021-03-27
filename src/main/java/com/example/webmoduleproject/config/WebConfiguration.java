package com.example.webmoduleproject.config;

import com.example.webmoduleproject.interceptors.SessionTimeOutInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final SessionTimeOutInterceptor sessionTimeOutInterceptor;

    public WebConfiguration(SessionTimeOutInterceptor sessionTimeOutInterceptor) {
        this.sessionTimeOutInterceptor = sessionTimeOutInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionTimeOutInterceptor);
    }
}
