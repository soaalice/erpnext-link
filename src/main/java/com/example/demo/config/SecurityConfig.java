package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/", "/supplier/**", "/supplier-quotation/**", 
                                "/purchase-order/**", "/purchase-invoice/**",
                                "/payment-entry/**")
                .excludePathPatterns("/auth/**", "/css/**", "/js/**");
    }
}
