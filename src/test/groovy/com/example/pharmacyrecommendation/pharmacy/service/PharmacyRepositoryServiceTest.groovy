package com.example.pharmacyrecommendation.pharmacy.service

import com.example.pharmacyrecommendation.AbstractIntegrationContainerBaseTest
import com.example.pharmacyrecommendation.pharmacy.entity.Pharmacy
import com.example.pharmacyrecommendation.pharmacy.repository.PharmacyRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

// 테스트 컨테이너를 이용한 스프링 부트 통합 테스트, 주소 업데이트 테스트, dirty checking test
class PharmacyRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    PharmacyRepositoryService pharmacyRepositoryService

    @Autowired
    PharmacyRepository pharmacyRepository

    // 모든 테스트 메서드 시작 전에 DB 초기화
    def setup() {
        pharmacyRepository.deleteAll()
    }

    // dirty checking 성공 테스트
    def "PharmacyRepository update - dirty checking success"() {
        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        when:
        // 약국 엔티티 저장
        def entity = pharmacyRepository.save(pharmacy)
        // 약국 엔티티 주소 수정(dirty checking o)
        pharmacyRepositoryService.updateAddress(entity.id, modifiedAddress)
        // 약국 엔티티 주소가 수정되었는지 확인하기 위해 가져오기
        def result = pharmacyRepository.findAll()

        then:
        // 약국 엔티티 주소가 수정되었는지 검증
        result.get(0).pharmacyAddress == modifiedAddress
    }

    // dirty checking 실패 테스트
    def "PharmacyRepository update - dirty checking fail"() {
        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        when:
        // 약국 엔티티 저장
        def entity = pharmacyRepository.save(pharmacy)
        // 약국 엔티티 주소 수정(dirty checking x)
        pharmacyRepositoryService.updateAddressWithoutTransaction(entity.id, modifiedAddress)
        // 약국 엔티티 주소가 수정되었는지 확인하기 위해 가져오기
        def result = pharmacyRepository.findAll()

        then:
        // 약국 엔티티 주소가 기존 주소랑 같은지 검증
        result.get(0).pharmacyAddress == inputAddress
    }

    // self invocation test
    def "self invocation"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        pharmacyRepositoryService.bar(Arrays.asList(pharmacy))

        then:
        // spock 에서 제공하는 예외 처리를 할 수 있는 키워드 thrown
        // when 블록에서 발생하는 예외를 잡는다.
        def e = thrown(RuntimeException.class)
        def result = pharmacyRepositoryService.findAll()
        result.size() == 1 // 트랜잭션이 적용되지 않는다(rollback 적용 x)
    }
}
