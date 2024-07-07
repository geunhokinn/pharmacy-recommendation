package com.example.pharmacyrecommendation.pharmacy.cache;

import com.example.pharmacyrecommendation.pharmacy.dto.PharmacyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRedisTemplateService {

    // 캐시 키 값을 약국으로 선언
    private static final String CACHE_KEY = "PHARMACY";

    // Redis 와 상호작용하고 자료 구조를 쉽게 다루기 위해 RedisTemplate 선언
    private final RedisTemplate<String, Object> redisTemplate;
    // 약국 데이터를 조회하거나 저장할 때 Serialization, Deserialization 하기 위해 의존성 주입
    private final ObjectMapper objectMapper;

    // 해쉬 자료구조 필드 생성(key : 캐시 키, field : pk, value : dto)
    private HashOperations<String, String, String> hashOperations;

    // 생성자 주입 이후 호출
    @PostConstruct
    public void init() {
        // 해시 자료 구조를 다루기 위한 HashOperations 객체 생성
        this.hashOperations = redisTemplate.opsForHash();
    }

    // redis 에 저장하는 메서드
    public void save(PharmacyDto pharmacyDto) {
        // pharmacyDto 가 null 이거나 id 가 null 이면 저정하지 않음
        if(Objects.isNull(pharmacyDto) || Objects.isNull(pharmacyDto.getId())) {
            log.error("Required Values must not be null");
            return;
        }

        try {
            // hash 자료 구조에 저장, PharmacyDto 객체를 JSON 문자열로 직렬화해서 저장
            hashOperations.put(CACHE_KEY, pharmacyDto.getId().toString(), serializePharmacyDto(pharmacyDto));
            log.info("[PharmacyRedisTemplateService save success] id: {}", pharmacyDto.getId());
        } catch (Exception e) {
            log.error("[PharmacyRedisTemplateService save error]: {}", e.getMessage());
        }
    }

    // redis 에서 조회하는 메서드
    public List<PharmacyDto> finalAll() {
        try {
            // Redis 에서 모든 PharmacyDto 객체를 조회하여 반환
            List<PharmacyDto> list = new ArrayList<>();
            for (String value : hashOperations.entries(CACHE_KEY).values()) {
                // JSON 문자열을 PharmacyDto 객체로 역직렬화해서 list 에 추가
                PharmacyDto pharmacyDto = deserializePharmacyDto(value);
                list.add(pharmacyDto);
            }
            return list;
        } catch (Exception e) {
            log.error("[PharmacyRedisTemplateService findAll error]: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // redis 에서 삭제하는 메서드, test 메서드를 사용하기 위해 추가
    public void delete(Long id) {
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[PharmacyRedisTemplateService delete] id: {}", id);
    }


    // dto 를 json 으로 직렬화하는 메서드
    private String serializePharmacyDto(PharmacyDto pharmacyDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(pharmacyDto);
    }

    // json 을 dto 로 역직렬화하는 메서드
    private PharmacyDto deserializePharmacyDto(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, PharmacyDto.class);
    }
}
