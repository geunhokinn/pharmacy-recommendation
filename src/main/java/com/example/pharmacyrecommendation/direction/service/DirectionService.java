package com.example.pharmacyrecommendation.direction.service;

import com.example.pharmacyrecommendation.api.dto.DocumentDto;
import com.example.pharmacyrecommendation.direction.entity.Direction;
import com.example.pharmacyrecommendation.pharmacy.dto.PharmacyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    // 약국 최대 검색 개수
    private static final int MAX_SEARCH_COUNT = 3;
    // 반경 10 km
    private static final double RADIUS_KM = 10.0;

    // 약국 데이터를 조회하고 dto 로 반환하는 메서드를 사용하기 위해 의존성 주입
    private final PharmacySearchService pharmacySearchService;

    // 고객에게 최대 3개의 약국을 안내(추천)하는 메서드, documentDto - 고객이 입력한 주소 정보
    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        // documentDto 가 null 이면 빈 리스트 반환
        if (Objects.isNull(documentDto)) return Collections.emptyList();

        return pharmacySearchService.searchPharmacyDtoList()
                .stream().map(pharmacyDto ->
                        Direction.builder()
                                .inputAddress(documentDto.getAddressName()) // 고객 주소
                                .inputLatitude(documentDto.getLatitude()) // 고객 위도
                                .inputLongitude(documentDto.getLongitude()) // 고객 경도
                                .targetPharmacyName(pharmacyDto.getPharmacyName()) // 약국 이름
                                .targetAddress(pharmacyDto.getPharmacyAddress()) // 약국 주소
                                .targetLatitude(pharmacyDto.getLatitude()) // 약국 위도
                                .targetLongitude(pharmacyDto.getLongitude()) // 약국 경도
                                .distance(
                                        calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                                pharmacyDto.getLatitude(), pharmacyDto.getLongitude())
                                )
                                .build()) // dto list 를 스트림으로 변환해서 Direction 엔티티로 만들기
                .filter(direction -> direction.getDistance() <= RADIUS_KM) // 반경 10km 이내인 Direction 엔티티만 필터
                .sorted(Comparator.comparing(Direction::getDistance)) // 거리로 오름차순 정렬
                .limit(MAX_SEARCH_COUNT) // 최대 3개로 제한
                .collect(Collectors.toList()); // 리스트로 변환
    }

    // Haversine formula 하버사인 포뮬러 알고리즘
    // 두 좌표 위도, 경도 사이의 거리를 구하는 메서드
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 라디안으로 변환
        lat1 = Math.toRadians(lat1); // 고객의 위도
        lon1 = Math.toRadians(lon1); // 고객의 경도
        lat2 = Math.toRadians(lat2); // 약국의 위도
        lon2 = Math.toRadians(lon2); // 약국의 경도

        double earthRadius = 6371; // Kilometers
        // 두 좌표의 거리 계산
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
