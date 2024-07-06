package com.example.pharmacyrecommendation.direction.service

import spock.lang.Specification

class Base62ServiceTest extends Specification {

    private Base62Service base62Service;

    // 모든 테스트 메서드 실행 전에 호출
    def setup() {
        base62Service = new Base62Service()
    }

    // base 62 인코딩, 디코딩 테스트 메서드
    def "check base62 encoder/decoder"() {
        given:
        // pk 를 5라고 주어짐
        long num = 5

        when:
        // 숫자를 인코딩
        def encoded = base62Service.encodeDirectionId(num)
        // 인코딩된 숫자를 디코딩
        def decodedId = base62Service.decodeDirectionId(encoded)

        then:
        // 주어진 값과 인코딩하고 디코딩한 값이 동일한지 검증
        num == decodedId
    }
}
