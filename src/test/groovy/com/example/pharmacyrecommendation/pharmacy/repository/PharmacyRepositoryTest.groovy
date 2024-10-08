package com.example.pharmacyrecommendation.pharmacy.repository

import com.example.pharmacyrecommendation.AbstractIntegrationContainerBaseTest
import com.example.pharmacyrecommendation.pharmacy.entity.Pharmacy
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

// 스프링 부트 통합 테스트 -> 상속 받아서 이미 통합 테스트 환경이 구축이 되었다.
//@SpringBootTest
class PharmacyRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    // 모든 테스트 메서드 시작 전에 DB 초기화
    def setup() {
        pharmacyRepository.deleteAll()
    }

    // 약국 엔티티 저장 테스트
    def "PharmacyRepository save"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11

        // 약국 엔티티 생성

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        // 약국 엔티티 저장
        def result = pharmacyRepository.save(pharmacy)

        then:
        // 저장된 데이터와 입력한 데이터가 일치하는지 검증
        result.getPharmacyAddress() == address
        result.getPharmacyName() == name
        result.getLatitude() == latitude
        result.getLongitude() == longitude
    }

    // 약국 엔티티 저장 테스트 2
    def "PharmacyRepository saveAll"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11

        // 약국 엔티티 생성

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        pharmacyRepository.saveAll(Arrays.asList(pharmacy))
        def result = pharmacyRepository.findAll()

        then:
        result.size() == 1
    }

    // BasTimeEntity 등록 후 매핑이 되는지 테스트
    def "BaseTimeEntity 등록"() {
        given:
        LocalDateTime now = LocalDateTime.now()
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .build()

        when:
        pharmacyRepository.save(pharmacy)
        def result = pharmacyRepository.findAll()

        then:
        // 미리 생성해둔 시간 이후에 엔티티를 저장한 시간인지 검증
        result.get(0).createdDate.isAfter(now)
        result.get(0).modifiedDate.isAfter(now)
    }
}
