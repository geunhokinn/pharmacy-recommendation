package com.example.pharmacyrecommendation.direction.controller

import com.example.pharmacyrecommendation.direction.service.DirectionService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DirectionControllerTest extends Specification {

    // 서블릿 컨테이너를 모킹해서 간단하기 컨트롤러를 테스트하기 위해 mockMvc 필드 생성
    private MockMvc mockMvc
    // DirectionController 를 테스트 하기 위해 DirectionService 를 모킹
    private DirectionService directionService = Mock()

    // 모든 테스트 메서드 실행 전에 호출
    def setup() {
        // DirectionController MockMvc 객체 생성
        mockMvc = MockMvcBuilders.standaloneSetup(new DirectionController(directionService)).build()
    }

    // shorten url 로 요청했을 때 정상적으로 리다이렉트하는지 확인하는 테스트
    def "GET /dir/{encodedId}"() {
        given:
        // encodedId (인코딩된 아이디)를 r 이라고 주어짐
        String encodedId = "r"
        // stubbing 하기 위한 리다이렉트 url 을 생성
        String redirectURL = "https://map.kakao.com/link/map/pharmacy,38.11,128.11"

        when:
        // directionService 의 findDirectionUrlById 메서드 결과를 stubbing
        directionService.findDirectionUrlById(encodedId) >> redirectURL
        // DirectionController 의 "/dir/{encodedId}" URI 를 get 방식으로 호출
        def result = mockMvc.perform(get("/dir/{encodedId}", encodedId))

        then:
        result.andExpect(status().is3xxRedirection()) // 리다이렉트 상태 코드 검증
            .andExpect(redirectedUrl(redirectURL)) // 리다이렉트 경로 검증
            .andDo(log())
    }
}
