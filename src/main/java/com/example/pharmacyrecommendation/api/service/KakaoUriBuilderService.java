package com.example.pharmacyrecommendation.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class KakaoUriBuilderService {

    // 주소 검색 api 기본 uri 상수로 선언
    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    // 카테고리 장소 검색 api 기본 uri 상수로 선언
    private static final String KAKAO_LOCAL_CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json";

    // 주소 검색 api 를 호출하기 위한 전체 uri 를 만드는 메서드
    public URI buildUriByAddressSearch(String address) {

        // spring 에서 제공하는 UriComponentsBuider 를 사용해 가독성을 높여 uri를 만들 수 있음
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        // 쿼리 파라미터 전달
        uriBuilder.queryParam("query", address);

        // 브라우저에서 해석할 수 없는 문자(ex 한글, 공백, 특수 문자)룰 안코딩 해야함
        URI uri = uriBuilder.build().encode().toUri();
        log.info("[KakaoUriBuilderService buildUriAddressSearch] address: {}, uri: {}", address, uri);

        // 완성된 uri 반환
        return uri;
    }

    // 카테고리로 장소 검색 api 를 호출하기 위한 전체 uri 를 만드는 메서드
    public URI buildUriByCategorySearch(double latitude, double longitude, double radius, String category) {

        double meterRadius = radius * 1000; // km -> m 단위로 변환

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_SEARCH_URL);
        uriBuilder.queryParam("category_group_code", category); // 카테고리
        uriBuilder.queryParam("x", longitude); // 경도
        uriBuilder.queryParam("y", latitude); // 위도
        uriBuilder.queryParam("radius", meterRadius); // 반경
        uriBuilder.queryParam("sort","distance"); // 거리 기준 정렬

        URI uri = uriBuilder.build().encode().toUri(); // uri 생성

        log.info("[KakaoAddressSearchService buildUriByCategorySearch] uri: {} ", uri);

        return uri;
    }
}
