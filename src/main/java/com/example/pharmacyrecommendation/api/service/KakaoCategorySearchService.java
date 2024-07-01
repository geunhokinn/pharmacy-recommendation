package com.example.pharmacyrecommendation.api.service;

import com.example.pharmacyrecommendation.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoCategorySearchService {

    private final RestTemplate restTemplate;  // kakao api 를 호출하기 위한 클라이언트 모듈 의존성 주입

    private final KakaoUriBuilderService kakaoUriBuilderService; // uri 를 만들기 위한 서비스 의존성 주입

    private static final String PHARMACY_CATEGORY = "PM9"; // 약국 카테고리

    @Value("${kakao.rest.api.key}") // 환경 변수로 등록한 api key 를 가져오기
    private String kakaoRestApiKey; // api key

    // 카테고리로 장소 검색 api 요청 처리 메서드
    public KakaoApiResponseDto requestPharmacyCategorySearch(double latitude, double longitude, double radius) {

        // uri 생성
        URI uri = kakaoUriBuilderService.buildUriByCategorySearch(latitude, longitude, radius, PHARMACY_CATEGORY);

        // spring 에서 제공하는 HttpHeaders 를 이용해서 간편하게 헤더 정보를 넣기
        HttpHeaders headers = new HttpHeaders();
        // 헤더에 api key 넣기
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK "+ kakaoRestApiKey);

        // 헤더를 HttpEntity 에 담아야 하므로 HttpEntity 생성
        HttpEntity httpEntity = new HttpEntity<>(headers);

        // kakao 카테고리로 장소 검색 api 호출하고 body 만 가져오기
        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class).getBody();
    }
}
