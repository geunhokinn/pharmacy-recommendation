package com.example.pharmacyrecommendation.api.service

import com.example.pharmacyrecommendation.AbstractIntegrationContainerBaseTest
import com.example.pharmacyrecommendation.api.dto.DocumentDto
import com.example.pharmacyrecommendation.api.dto.KakaoApiResponseDto
import com.example.pharmacyrecommendation.api.dto.MetaDto
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

// 테스트 컨테이너를 이용한 스프링 부트 통합 테스트, kakao 주소 검색 api retry 테스트
class KakaoAddressSearchServiceRetryTest extends AbstractIntegrationContainerBaseTest {

    // kakao 주소 검색 api retry 테스트를 하기 위해 KakaoAddressSearchService 주입
    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    // spock 에서 @SpringBean 을 사용해서 스프링 컨테이너 내에 있는 빈을 모킹할 수 있음
    // 실제 카카오 api 를 호출하는 대신, 로컬호스트에서 모킹 서버를 호출하기 하기 위해 @SpringBean 을 사용
    @SpringBean
    private KakaoUriBuilderService kakaoUriBuilderService = Mock()

    // 실패 응답값을 보내기 위해 MockWebServer 필드 생성
    private MockWebServer mockWebServer

    // java 객체를 json 문자열로 직렬화하기 위해 ObjectMapper 생성
    private ObjectMapper mapper = new ObjectMapper()

    // 유저 입력 주소
    private String inputAddress = "서울 성북구 종암로 10길"

    // 모든 테스트 메서드 실행 전에 호출
    def setup() {
        mockWebServer = new MockWebServer() // MockWebServer 생성
        mockWebServer.start() // MockWebServer 시작
        println mockWebServer.port // MockWebServer 실행 시 port 가 동적으로 자동 할당 됨 ex.51056
        println mockWebServer.url("/") // // MockWebServer 실행 시 url 은 localhost 에서 실행 됨 ex. http://localhost:51056/
    }

    // 모든 테스트 메서드 실행 후에 호출
    def cleanup() {
        mockWebServer.shutdown() // MockWebServer 다운
    }

    // exception 이 발생했을 때 retry 가 정상적으로 적용되는지 확인 테스트
    def "requestAddressSearch retry success"() {
        given:
        // 카카오 api 를 호출하고 응답을 받는 것처럼 예상 응답 KakaoApiResponseDto 를 만듦
        def metaDto = new MetaDto(1)
        def documentDto = DocumentDto.builder()
                .addressName(inputAddress)
                .build()
        def expectedResponse = new KakaoApiResponseDto(metaDto, Arrays.asList(documentDto))
        def uri = mockWebServer.url("/").uri() // HttpUrl 객체를 URI 객체로 변환

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504)) // MockWebServer 가 504 에러를 응답하도록 큐에 추가해서 응답을 미리 설정
        mockWebServer.enqueue(new MockResponse().setResponseCode(200) // MockWebServer 가 200 성공을 응답하도록 큐에 추가해서 응답을 미리 설정
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 응답 헤더에 Content-Type: application/json 을 추가
                .setBody(mapper.writeValueAsString(expectedResponse))) // java 객체를 json 문자열로 직렬화해서 응답 바디에 추가
        // 테스트 클라이언트가 서버에 요청을 보낼 때 이 큐에서 순차적으로 응답을 꺼내어 반환

        def kakaoApiResult = kakaoAddressSearchService.requestAddressSearch(inputAddress) // 주소 검색 요청 메서드 호출

        then:
        // buildUriByAddressSearch() 가 2번 호출됐는지 검증하고 uri 을 반환했는지 검증
        // then 블록에서 스터빙을 정의해도 when 블록의 코드가 이를 인식하고 동작함
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri
        // 예상 응답 정보와 일치하는지 검증
        kakaoApiResult.getDocumentDtoList().size() == 1
        kakaoApiResult.getMetaDto().totalCount == 1
        kakaoApiResult.getDocumentDtoList().get(0).getAddressName() == inputAddress
    }

    // retry 가 모두 실패했을 때 recover() 가 실행되는지 확인 테스트
    def "requestAddressSearch retry fail "() {
        given:
        def uri = mockWebServer.url("/").uri() // HttpUrl 객체를 URI 객체로 변환

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504)) // MockWebServer 가 504 에러를 응답하도록 큐에 추가해서 응답을 미리 설정
        mockWebServer.enqueue(new MockResponse().setResponseCode(504)) // MockWebServer 가 504 에러를 응답하도록 큐에 추가해서 응답을 미리 설정
        // 테스트 클라이언트가 서버에 요청을 보낼 때 이 큐에서 순차적으로 응답을 꺼내어 반환

        def result = kakaoAddressSearchService.requestAddressSearch(inputAddress) // 주소 검색 요청 메서드 호출

        then:
        // buildUriByAddressSearch() 가 2번 호출됐는지 검증하고 uri 을 반환했는지 검증
        // then 블록에서 스터빙을 정의해도 when 블록의 코드가 이를 인식하고 동작함
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri
        // 모두 실패했기 때문에 null 을 반환하는지 검증
        result == null
    }
}
