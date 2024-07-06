package com.example.pharmacyrecommendation.direction.service;

import com.example.pharmacyrecommendation.api.dto.DocumentDto;
import com.example.pharmacyrecommendation.api.service.KakaoCategorySearchService;
import com.example.pharmacyrecommendation.direction.entity.Direction;
import com.example.pharmacyrecommendation.direction.repository.DirectionRepository;
import com.example.pharmacyrecommendation.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

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
    // 길 안내 url
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

    // 약국 데이터를 조회하고 dto 로 반환하는 메서드를 사용하기 위해 의존성 주입
    private final PharmacySearchService pharmacySearchService;
    // 약국 안내(추천) 결과를 저장하는 메서드를 사용하기 위해 의존성 주입
    private final DirectionRepository directionRepository;
    // 카테고리로 장소 검색 api 요청 메서드를 호출하기 위해 의존성 주입
    private final KakaoCategorySearchService kakaoCategorySearchService;
    // pk 를 인코딩과 디코딩하는 메서드를 호출하기 위해 의존성 주입
    private final Base62Service base62Service;

    // 약국 안내(추천) 결과를 저장하는 메서드
    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {

        // directionList 가 null 이면 빈 리스트 반환
        if(CollectionUtils.isEmpty(directionList)) return Collections.emptyList();

        // 약국 안내(추천) 결과 저장
        return directionRepository.saveAll(directionList);
    }

    // 인코딩된 pk 가 입력되면 pk 를 디코딩해서 엔티티를 조회하는 메서드
    public String findDirectionUrlById(String encodedId) {
        Long decodedId = base62Service.decodeDirectionId(encodedId);
        Direction direction = directionRepository.findById(decodedId).orElse(null);

        // 길 안내 파라미터 생성
        String params = String.join(",", direction.getTargetPharmacyName(),
                String.valueOf(direction.getTargetLatitude()), String.valueOf(direction.getTargetLongitude()));

        // UriComponentsBuilder 객체를 String 으로 변환하며 자동으로 인코딩
        String result = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params)
                .toUriString();

        return result;
    }

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

    // 공공 기관 데이터를 사용하는 것이 아닌 카테고리로 장소 검색 api 로 데이터를 찾고 최대 3개의 약국을 안내(추천)하는 메서드
    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto) {
        if(Objects.isNull(inputDocumentDto)) return Collections.emptyList();

        return kakaoCategorySearchService
                .requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
                .getDocumentDtoList() // 가까운 약국 리스트
                .stream().map(resultDocumentDto ->
                        Direction.builder()
                                .inputAddress(inputDocumentDto.getAddressName()) // 고객 주소 이름
                                .inputLatitude(inputDocumentDto.getLatitude()) // 고객 위도
                                .inputLongitude(inputDocumentDto.getLongitude()) // 고객 경도
                                .targetPharmacyName(resultDocumentDto.getPlaceName()) // 약국 이름
                                .targetAddress(resultDocumentDto.getAddressName()) // 약국 주소 이름
                                .targetLatitude(resultDocumentDto.getLatitude()) // 약국 위도
                                .targetLongitude(resultDocumentDto.getLongitude()) // 약국 경도
                                .distance(resultDocumentDto.getDistance() * 0.001) // m -> km 단위로 변환
                                .build())
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
