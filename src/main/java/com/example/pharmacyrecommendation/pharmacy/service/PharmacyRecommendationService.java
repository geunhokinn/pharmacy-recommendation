package com.example.pharmacyrecommendation.pharmacy.service;

import com.example.pharmacyrecommendation.api.dto.DocumentDto;
import com.example.pharmacyrecommendation.api.dto.KakaoApiResponseDto;
import com.example.pharmacyrecommendation.api.service.KakaoAddressSearchService;
import com.example.pharmacyrecommendation.direction.dto.OutputDto;
import com.example.pharmacyrecommendation.direction.entity.Direction;
import com.example.pharmacyrecommendation.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// 최종적으로 약국 안내(추천)를 하는 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    // 최종적으로 가까운 약국을 찾고 약국 안내(추천)를 저장하는 메서드
    public List<OutputDto> recommendPharmacyList(String address) {

        // 고객의 문자열 주소를 카카오 api 를 통해서 위치 기반 데이터로 변경
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        // retry 가 모두 실패하거나 주소를 검색했는데 위도, 경도가 맵핑되지 않을 때 null 을 반환할 수 있음
        if(Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentDtoList())) {
            log.error("[KakaoAddressSearchService recommendPharmacyList fail] Input address: {}", address);
            return Collections.emptyList();
        }

        // documentDtoList 에서 첫 번째 값을 기준으로 사용
        DocumentDto documentDto = kakaoApiResponseDto.getDocumentDtoList().get(0);

        // 가까운 약국 리스트를 찾기
//        List<Direction> directionList = directionService.buildDirectionList(documentDto);
        // 공공기관 데이터와 거리 계산 알고리즘을 사용하지 않고 api 를 통해서 약국을 안내(추천)
        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);

        // 찾은 약국 리스트를 데이터 베이스에 저장하고 컨트롤러에 dto 로 반환
        return directionService.saveAll(directionList)
                .stream()
                .map(this::convertToOutputDto)
                .collect(Collectors.toList());
    }

    // 엔티티를 dto 로 변환하는 메서드
    private OutputDto convertToOutputDto(Direction direction) {
        return OutputDto.builder()
                .pharmacyName(direction.getTargetPharmacyName())
                .pharmacyAddress(direction.getTargetAddress())
                .directionUrl("todo")
                .roadViewUrl("todo")
                .roadViewUrl("todo")
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();
    }
}
