package com.example.pharmacyrecommendation.direction.service

import com.example.pharmacyrecommendation.api.dto.DocumentDto
import com.example.pharmacyrecommendation.pharmacy.dto.PharmacyDto
import com.example.pharmacyrecommendation.pharmacy.service.PharmacySearchService
import spock.lang.Specification

class DirectionServiceTest extends Specification {

    // spock 에서 Mock() 을 이용해서 목 객체 생성
    private PharmacySearchService pharmacySearchService = Mock()

    // DirectionService 테스트
    private DirectionService directionService = new DirectionService(pharmacySearchService)

    // 샘플 약국 데이터 리스트
    private List<PharmacyDto> pharmacyList

    // 모든 메서드 시작 전에 샘플 데이터 넣기, 10 km 이내에 있는 약국들을 세팅
    def setup() {
        pharmacyList = new ArrayList()
        pharmacyList.addAll(
                PharmacyDto.builder()
                        .id(1L)
                        .pharmacyName("돌곶이온누리약국")
                        .pharmacyAddress("주소1")
                        .latitude(37.61040424)
                        .longitude(127.0569046)
                        .build(),
                PharmacyDto.builder()
                        .id(2L)
                        .pharmacyName("호수온누리약국")
                        .pharmacyAddress("주소2")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build()
        )
    }

    def "buildDirectList - 결과 값이 거리 순으로 정렬이 되는지 확인"() {
        given:
        // 고객 입력 정보
        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:
        // mock 객체가 dto list 를 조회해서 가져오는 것처럼 stubbing 하기
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyList

        def results = directionService.buildDirectionList(documentDto)

        then:
        // 샘플 데이터가 모두 10km 이내에 있고 호수 약국이 돌곶이 약국보다 가까이 있음
        results.size() == 2
        results.get(0).targetPharmacyName == "호수온누리약국"
        results.get(1).targetPharmacyName == "돌곶이온누리약국"
    }

    def "buildDirectList - 정해진 반경 10km 내에 검색이 되는지 확인"() {
        given:
        // 10km 이외에 있는 약국을 세팅
        pharmacyList.add(
                PharmacyDto.builder()
                        .id(3L)
                        .pharmacyName("경기약국")
                        .pharmacyAddress("주소3")
                        .latitude(37.3825107393401)
                        .longitude(127.236707811313)
                        .build())

        // 고객 입력 정보
        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:
        // mock 객체가 dto list 를 조회해서 가져오는 것처럼 stubbing 하기
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyList

        def results = directionService.buildDirectionList(documentDto)

        then:
        // 10km 이외의 약국은 filter 에 의해 걸러지고 호수 약국이 돌곶이 약국보다 가까이 있음
        results.size() == 2
        results.get(0).targetPharmacyName == "호수온누리약국"
        results.get(1).targetPharmacyName == "돌곶이온누리약국"
    }
}
