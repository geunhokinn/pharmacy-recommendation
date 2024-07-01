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

    // 다양한 주소 검색에 대한 테스트
    def "정상적인 주소를 입력했을 경우, 정상적인 위도, 경로로 변환된다."() {

        given:
        // 실제 결과 값을 false 로 미리 지정
        boolean actualResult = false

        when:
        // 카카오 주소 검색 api 를 호출한 결과값
        def searchResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        // 결과값이 null 이면 false
        // 정상적으로 결과 값이 있으면 documentDtoList 의 사이즈가 0 보다 크므로 true
        if (searchResult == null) actualResult = false
        else actualResult = searchResult.getDocumentDtoList().size() > 0

        // 실제 값과 기대 값이 같은지 검증
        actualResult == expectedResult

        where:
        // where 블록에서 사용할 파라미터를 정의하고 기대되는 결과 값을 적을 수 있음
        // data table
        inputAddress    |   expectedResult
        "서울 특별시 성북구 종암동"                   | true
        "서울 성북구 종암동 91"                     | true
        "서울 대학로"                             | true
        "서울 성북구 종암동 잘못된 주소"               | false // 잘못된 주소이므로 결과값이 null
        "광진구 구의동 251-45"                     | true
        "광진구 구의동 251-455555"                 | false // 잘못된 주소이므로 결과값이 null
        ""                                      | false // empty value 이므로 결과값이 null
    }
}
