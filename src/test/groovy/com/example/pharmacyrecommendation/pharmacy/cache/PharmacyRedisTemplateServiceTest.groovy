package com.example.pharmacyrecommendation.pharmacy.cache

import com.example.pharmacyrecommendation.AbstractIntegrationContainerBaseTest
import com.example.pharmacyrecommendation.pharmacy.dto.PharmacyDto
import org.springframework.beans.factory.annotation.Autowired

// redis 로 저장, 조회, 삭제를 해야 하기 때문에 테스트 컨테이너를 이용한 스프링 부트 통합 테스트를 진행
class PharmacyRedisTemplateServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRedisTemplateService pharmacyRedisTemplateService

    // 모든 테스트 메서드 실행 전에 호출
    def setup() {
        // Redis 에 모든 데이터를 조회하고 모든 데이터를 삭제
        pharmacyRedisTemplateService.finalAll()
        .forEach(dto -> {
            pharmacyRedisTemplateService.delete(dto.getId())
        })
    }

    // Redis 저장 성공 테스트
    def "save success"() {
        given:
        // 샘플 데이터 생성
        String pharmacyName = "name"
        String pharmacyAddress = "address"
        PharmacyDto dto = PharmacyDto.builder()
                .id(1L)
                .pharmacyName(pharmacyName)
                .pharmacyAddress(pharmacyAddress)
                .build()

        when:
        // 약국 dto 를 Redis 에 저장
        pharmacyRedisTemplateService.save(dto)
        // Redis 에서 약국 dto 조회
        List<PharmacyDto> result = pharmacyRedisTemplateService.finalAll()

        then:
        // 샘플 데이터가 1개이므로 사이즈가 1인지 검증
        result.size() == 1
        // 미리 생성한 샘플 데이터와 일치하는지 검증
        result.get(0).id == 1L
        result.get(0).pharmacyName == pharmacyName
        result.get(0).pharmacyAddress == pharmacyAddress
    }

    // Redis 저장 실패 테스트
    def "success fail"() {
        given:
        // 모든 값이 null 인 샘플 데이터 생성
        PharmacyDto dto = PharmacyDto.builder().build()

        when:
        // 약국 dto 를 Redis 에 저장
        pharmacyRedisTemplateService.save(dto)
        // Redis 에서 약국 dto 조회
        List<PharmacyDto> result = pharmacyRedisTemplateService.finalAll()

        then:
        // id 가 null 인 조건으로 Redis 에 데이터가 저장되지 않으므로 사이즈가 0인지 검증
        result.size() == 0
    }

    // Redis 삭제 테스트
    def "delete"() {
        given:
        // 샘플 데이터 생성
        String pharmacyName = "name"
        String pharmacyAddress = "address"
        PharmacyDto dto = PharmacyDto.builder()
                .id(1L)
                .pharmacyName(pharmacyName)
                .pharmacyAddress(pharmacyAddress)
                .build()

        when:
        // 약국 dto 를 Redis 에 저장
        pharmacyRedisTemplateService.save(dto)
        // dto 의 id 값으로 Redis 데이터를 삭제
        pharmacyRedisTemplateService.delete(dto.getId())
        // Redis 에서 약국 dto 조회
        List<PharmacyDto> result = pharmacyRedisTemplateService.finalAll()

        then:
        // 데이터를 삭제했으므로 사이즈가 0인지 검증
        result.size() == 0
    }
}
