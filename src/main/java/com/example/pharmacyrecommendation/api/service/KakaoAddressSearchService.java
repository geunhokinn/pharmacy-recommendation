package com.example.pharmacyrecommendation.api.service;

import com.example.pharmacyrecommendation.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAddressSearchService {

    private final RestTemplate restTemplate;  // kakao api 를 호출하기 위한 클라이언트 모듈 의존성 주입
    private final KakaoUriBuilderService kakaoUriBuilderService; // uri 를 만들기 위한 서비스 의존성 주입

    @Value("${kakao.rest.api.key}") // 환경 변수로 등록한 api key 를 가져오기
    private String kakaoRestApiKey; // api key

    // 주소 검색 요청 처리 메서드
    @Retryable(
            value = {RuntimeException.class}, // RuntimeException 발생 시 retry
            maxAttempts = 2, // 최대 2번 시도
            backoff = @Backoff(delay = 2000) // 딜레이 시간 2초
    ) // spring retry 적용
    public KakaoApiResponseDto requestAddressSearch(String address) {

        // address null or empty value 가 올 수 있으므로 validation 체크
        if (ObjectUtils.isEmpty(address)) return null;

        // uri 생성
        URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);

        // spring 에서 제공하는 HttpHeaders 를 이용해서 간편하게 헤더 정보를 넣기
        HttpHeaders headers = new HttpHeaders();
        // 헤더에 api key 넣기
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);

        // 헤더를 HttpEntity 에 담아야 하므로 HttpEntity 생성
        HttpEntity httpEntity = new HttpEntity<>(headers);

        // kakao api 호출
        // 파라미터 : uri, method, requestEntity, responseType
        // 응답을 dto 에 매핑하고 body 만 가져오기
        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class).getBody();
    }

    // requestAddressSearch() 메서드의 retry 에 대한 Fallback 처리
    @Recover
    public KakaoApiResponseDto recover(RuntimeException e, String address) {
        log.error("All the retries failed. address: {}, error: {}", address, e.getMessage());
        return null;
    }
}
