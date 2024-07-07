package com.example.pharmacyrecommendation.pharmacy.service

import com.example.pharmacyrecommendation.pharmacy.cache.PharmacyRedisTemplateService
import com.example.pharmacyrecommendation.pharmacy.entity.Pharmacy
import spock.lang.Specification

class PharmacySearchServiceTest extends Specification {

    // PharmacySearchService 테스트
    private PharmacySearchService pharmacySearchService

    // Mock() 을 이용해서 목 객체 생성
    private PharmacyRepositoryService pharmacyRepositoryService = Mock()
    private  PharmacyRedisTemplateService pharmacyRedisTemplateService = Mock()

    // 샘플 약국 데이터 리스트
    private List<Pharmacy> pharmacyList

    // 모든 테스트 메서드 실행 전에 호출
    def setup() {
        pharmacySearchService = new PharmacySearchService(pharmacyRepositoryService, pharmacyRedisTemplateService)

        pharmacyList = new ArrayList()
        pharmacyList.addAll(
                Pharmacy.builder()
                        .id(1L)
                        .pharmacyName("돌곶이온누리약국")
                        .pharmacyAddress("주소1")
                        .latitude(37.61040424)
                        .longitude(127.0569046)
                        .build(),
                Pharmacy.builder()
                        .id(2L)
                        .pharmacyName("호수온누리약국")
                        .pharmacyAddress("주소2")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build()
        )
    }

    // 레디스 장애 시 DB를 이용하여 약국 데이터를 조회하는지 확인하는 테스트
    def "레디스 장애 시 DB를 이용하여 약국 데이터 조회"() {
        when:
        // redis 에서 빈 리스트를 반환하도록 stubbing
        pharmacyRedisTemplateService.finalAll() >> []
        // db 에서 미리 생성한 샘플 데이터를 반환하도록 stubbing
        pharmacyRepositoryService.findAll() >> pharmacyList

        // db 에서 약국 데이터 조회
        def result = pharmacySearchService.searchPharmacyDtoList()

        then:
        // list 의 크기가 2인지 검증
        result.size() == 2
    }
}
