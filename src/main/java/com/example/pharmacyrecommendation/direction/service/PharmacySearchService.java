package com.example.pharmacyrecommendation.direction.service;

import com.example.pharmacyrecommendation.pharmacy.dto.PharmacyDto;
import com.example.pharmacyrecommendation.pharmacy.entity.Pharmacy;
import com.example.pharmacyrecommendation.pharmacy.service.PharmacyRepositoryService;
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

    private final PharmacyRepositoryService pharmacyRepositoryService;

    // 약국 데이터를 조회하고 dto 로 반환하는 메서드
    public List<PharmacyDto> searchPharmacyDtoList() {

        // redis

        // db
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
