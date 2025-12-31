package com.example.onlinelearning.config;

import com.example.onlinelearning.security.BindingStatusInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BindingWebConfig implements WebMvcConfigurer {

    private final BindingStatusInterceptor bindingStatusInterceptor;

    public BindingWebConfig(BindingStatusInterceptor bindingStatusInterceptor) {
        this.bindingStatusInterceptor = bindingStatusInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bindingStatusInterceptor)
                .addPathPatterns("/**");
    }
}
