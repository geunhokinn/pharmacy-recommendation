package com.example.pharmacyrecommendation.pharmacy.controller;

import com.example.pharmacyrecommendation.pharmacy.cache.PharmacyRedisTemplateService;
import com.example.pharmacyrecommendation.pharmacy.dto.PharmacyDto;
import com.example.pharmacyrecommendation.pharmacy.service.PharmacyRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PharmacyController {

    // db 에서 약국 데이터를 조회하기 위해 의존성 주입
    private final PharmacyRepositoryService pharmacyRepositoryService;
    // redis 에 약국 데이터를 저장하기 위해 의존성 주입
    private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

    // 데이터 초기 셋팅을 위한 임시 메서드, 데이터베이스에 있는 약국 데이터를 Redis 에 동기화
    @GetMapping("/redis/save")
    public String save() {

        // DB 에서 약국 데이터를 조회하고 entity 를 dto 로 변환
        List<PharmacyDto> pharmacyDtoList = pharmacyRepositoryService.findAll()
                .stream().map(pharmacy -> PharmacyDto.builder()
                        .id(pharmacy.getId())
                        .pharmacyName(pharmacy.getPharmacyName())
                        .pharmacyAddress(pharmacy.getPharmacyAddress())
                        .latitude(pharmacy.getLatitude())
                        .longitude(pharmacy.getLongitude())
                        .build())
                .toList();

        // pharmacyDtoList 를 Redis 에 저장
        pharmacyDtoList.forEach(pharmacyRedisTemplateService::save);

        return "success";
    }
}
