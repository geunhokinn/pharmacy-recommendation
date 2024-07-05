package com.example.pharmacyrecommendation.direction.controller

import com.example.pharmacyrecommendation.direction.dto.OutputDto
import com.example.pharmacyrecommendation.pharmacy.service.PharmacyRecommendationService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class FormControllerTest extends Specification {

    // 서블릿 컨테이너를 모킹해서 간단하기 컨트롤러를 테스트하기 위해 mockMvc 필드 생성
    private MockMvc mockMvc;
    // FormController 를 테스트 하기 위해 PharmacyRecommendationService 를 모킹
    private PharmacyRecommendationService pharmacyRecommendationService = Mock()
    // PharmacyRecommendationService 의 recommendPharmacyList() 메서드 호출 결과를 stubbing 하기 위해 필드 생성
    private List<OutputDto> outputDtoList

    // 모든 테스트 메서드 실행 전에 호출
    def setup() {
        // FromController MockMvc 객체 생성
        mockMvc = MockMvcBuilders.standaloneSetup(new FormController(pharmacyRecommendationService)).build()
        // stubbing 전에 샘플 데이터를 넣기
        outputDtoList = new ArrayList<>()
        outputDtoList.addAll(
            OutputDto.builder()
                .pharmacyName("pharmacy1")
                .build(),
            OutputDto.builder()
                .pharmacyName("pharmacy2")
                .build()
        )
    }

    // main 화면 컨트롤러 메서드 테스트
    def "GET /"() {
        // 간단한 테스트의 경우 expect 블록 하나로 가능
        expect:
        // FormController 의 "/" URI 를 get 방식으로 호출
        mockMvc.perform(get("/")) // get 으로 요청 전송
        // 응답 검증
            .andExpect(handler().handlerType(FormController.class)) // 핸들러 타입 검증
            .andExpect(handler().methodName("main")) // 메서드 이름 검증
            .andExpect(status().isOk()) // 상태 코드 검증
            .andExpect(view().name("main")) // view name 검증
            .andDo(log()) // 로그 확인
    }

    // 약국 안내(추천) 컨트롤러 메서드 테스트
    def "POST /search"() {
        given:
        String inputAddress = "서울 성북구 종암동"

        when:
        // FormController 의 "/search" URI 를 address 파라미터를 넣어서 post 방식으로 호출
        def resultActions = mockMvc.perform(post("/search")
                .param("address", inputAddress))

        then:
        // recommendPharmacyList() 가 1번 호출됐는지 검증하고 outputDtoList 을 반환했는지 검증
        1 * pharmacyRecommendationService.recommendPharmacyList(argument -> {
            assert argument == inputAddress // 파라미터가 inputAddress 인지 검증
        }) >> outputDtoList // stubbing 도 동시에 이뤄짐

        resultActions
            .andExpect(status().isOk()) // 상태 코드 검증
            .andExpect(view().name("output")) // view name 검증
            .andExpect(model().attributeExists("outputFormList")) // model 에 outputFormList 라는 key 가 존재하는지 검증
            .andExpect(model().attribute("outputFormList", outputDtoList)) // outputFormList key 에 outputDtoList value 객체가 맞는지 검증
            .andDo(log()) // 로그 확인
    }
}
