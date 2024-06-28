package com.example.pharmacyrecommendation.api.service

import com.example.pharmacyrecommendation.AbstractIntegrationContainerBaseTest
import com.example.pharmacyrecommendation.api.dto.KakaoApiResponseDto
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

// 테스트 컨테이너를 이용한 스프링 부트 통합 테스트, kakao 주소 검색 api 테스트
class KakaoAddressSearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    def "address 파라미터 값이 null 이면, requestAddressSearch 메서드는 null 을 리턴한다."() {
        given:
        def address = null

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address)

        then:
        result == null
    }

    def "주소값이 valid 하다면, requestAddressSearch 메서드는 정상적으로 document 를 반환한다."() {
        given:
        def address = "서울 성북구 종암로 10길"

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address)

        then:
        // groovy 문법으로 간단하게 getter 를 호출할 수 있다.
        result.documentDtoList.size() > 0 // groovy style
        result.metaDto.totalCount > 0
        // 최소한 한 개는 있어야 됨
        result.documentDtoList.get(0).addressName != null
    }
}
