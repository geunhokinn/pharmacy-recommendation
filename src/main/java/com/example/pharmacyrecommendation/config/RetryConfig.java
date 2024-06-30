package com.example.pharmacyrecommendation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;

@EnableRetry // spring retry 활성화
@Configuration
public class RetryConfig {

    // retry template 를 이용한 방법
//    @Bean
//    public RetryTemplate retryTemplate() {
//        return new RetryTemplate();
//    }
}
