package com.example.pharmacyrecommendation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // application.yml 에 정의된 프로퍼티 값 주입
    @Value("${spring.data.redis.host}")
    private String redisHost;

    // application.yml 에 정의된 프로퍼티 값 주입
    @Value("${spring.data.redis.port}")
    private int redisPort;

    // 레디스와 연결하기 위해 Lettuce 클라이언트를 사용하는 LettuceConnectionFactory 를 빈으로 등록
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    } // 호스트와 포트를 사용하여 Redis 서버와의 연결을 설정

    // Redis 데이터베이스와 상호작용을 간편하게 하기 위해 RedisTemplate 을 빈으로 등록
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // RedisTemplate 객체 생성
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // redisConnectionFactory() 를 RedisTemplate 에 설정하므로 Redis 서버와 연결할 수 있음
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // 키, 해시 키, 해시 값을 모두 문자열로 직렬화 및 역직렬하기 위해 시리얼라이저 설정
        // RedisTemplate 의 키 시리얼라이저 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // RedisTemplate 의 해시 키 시리얼라이저 설정
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // RedisTemplate 의 해시 값 시리얼라이저 설정
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
