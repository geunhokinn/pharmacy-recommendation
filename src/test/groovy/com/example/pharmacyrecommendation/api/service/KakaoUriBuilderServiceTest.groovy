package com.example.pharmacyrecommendation.api.service

import spock.lang.Specification

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

// spock 프레임워크를 이용한 buildUriByAddressSearch() 메서드 단위 테스트
class KakaoUriBuilderServiceTest extends Specification {

    private KakaoUriBuilderService kakaoUriBuilderService

    // 모든 메서드 시작 전에 실행
    def setup() {
        kakaoUriBuilderService = new KakaoUriBuilderService()
    }

    def "buildUriByAddressSearch - 한글 파라미터의 경우 정상적으로 인코딩"() {
        given:
        String address = "서울 성북구"
        def charset = StandardCharsets.UTF_8

        // def 키워드를 사용해서 동적으로 타입을 확인해서 받을 수 있음
        when:
        def uri = kakaoUriBuilderService.buildUriByAddressSearch(address)
        // 디코딩 할 때 string 과 charset 이 필요
        def decodeResult = URLDecoder.decode(uri.toString(), charset)

        // 원래 uri 가 나오면 성공
        // then 블록의 조건이 참이면 성공
        then:
        decodeResult == "https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구"
    }
}
