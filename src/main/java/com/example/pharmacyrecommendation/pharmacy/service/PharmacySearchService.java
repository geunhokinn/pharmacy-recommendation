package com.example.pharmacyrecommendation.pharmacy.service;

import com.example.pharmacyrecommendation.pharmacy.cache.PharmacyRedisTemplateService;
import com.example.pharmacyrecommendation.pharmacy.dto.PharmacyDto;
import com.example.pharmacyrecommendation.pharmacy.entity.Pharmacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// redis or db 에서 약국 데이터를 조회(검색)할 수 있는 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacySearchService {

    // db 에서 약국 데이터를 조회하기 위해 의존성 주입
    private final PharmacyRepositoryService pharmacyRepositoryService;
    // redis 에서 약국 데이터를 조회하기 위해 의존성 주입
    private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

    // 약국 데이터를 조회하고 dto 로 반환하는 메서드
    public List<PharmacyDto> searchPharmacyDtoList() {

        // redis
        // 모든 약국 데이터 조회
        List<PharmacyDto> pharmacyDtoList = pharmacyRedisTemplateService.finalAll();
        // 약국 데이터가 있으면 redis 에 있는 데이터를 바로 사용
        if (!pharmacyDtoList.isEmpty()) {
            log.info("redis findAll success");
            return pharmacyDtoList;
        }

        // redis 에 문제가 있거나 장애가 있을 경우 db 를 사용

        // db
        // 모든 약국 데이터 조회
        return pharmacyRepositoryService.findAll()
                .stream()
                .map(this::convertTopharmacyDto)
                .collect(Collectors.toList());
    }

    // 엔티티를 Dto 로 변환하는 메서드
    private PharmacyDto convertTopharmacyDto(Pharmacy pharmacy) {

        return PharmacyDto.builder()
                .id(pharmacy.getId())
                .pharmacyAddress(pharmacy.getPharmacyAddress())
                .pharmacyName(pharmacy.getPharmacyName())
                .latitude(pharmacy.getLatitude())
                .longitude(pharmacy.getLongitude())
                .build();
    }
}
