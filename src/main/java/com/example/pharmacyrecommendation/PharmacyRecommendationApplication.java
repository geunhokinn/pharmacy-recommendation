package com.example.pharmacyrecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // jpa auditing 기능 활성화
@SpringBootApplication
public class PharmacyRecommendationApplication extends BaseTimeEntity {

    public static void main(String[] args) {
        SpringApplication.run(PharmacyRecommendationApplication.class, args);
    }
}
