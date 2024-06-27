package com.example.pharmacyrecommendation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean // kakao api 를 호출하기 위한 http 클라이언트 모듈
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
