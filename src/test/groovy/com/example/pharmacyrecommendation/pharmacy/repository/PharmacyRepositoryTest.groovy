package com.example.pharmacyrecommendation.pharmacy.repository

import com.example.pharmacyrecommendation.AbstractIntegrationContainerBaseTest
import com.example.pharmacyrecommendation.pharmacy.entity.Pharmacy
import org.springframework.beans.factory.annotation.Autowired

// 스프링 부트 통합 테스트 -> 상속 받아서 이미 통합 테스트 환경이 구축이 되었다.
//@SpringBootTest
class PharmacyRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRepository pharmacyRepository;

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
}
