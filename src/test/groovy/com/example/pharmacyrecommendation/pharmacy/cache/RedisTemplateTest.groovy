package com.example.pharmacyrecommendation.pharmacy.cache

import com.example.pharmacyrecommendation.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

// redis 를 사용해야 하기 때문에 테스트 컨테이너를 이용한 스프링 부트 통합 테스트를 진행
class RedisTemplateTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private RedisTemplate redisTemplate

    // String 자료 구조 테스트
    def "RedisTemplate String operations"() {
        given:
        // String 자료 구조를 다루기 위해 ValueOperations 인터페이스 인스턴스 생성
        def valueOperations = redisTemplate.opsForValue()
        def key = "stringKey"
        def value = "hello"

        when:
        // String 자료 구조에 저장, key 에 value 가 저장됨
        valueOperations.set(key, value)

        then:
        // key 로 value 조회
        def result = valueOperations.get(key)
        // result 와 value 가 동일한지 검증
        result == value
    }

    // Set 자료 구조 테스트
    def "RedisTemplate set operations"() {
        // Set 자료 구조를 다루기 위해 SetOperations 인터페이스 인스턴스 생성
        given:
        def setOperations = redisTemplate.opsForSet()
        def key = "setKey"

        when:
        // Set 자료 구조에 저장, key 에 value 들이 저장됨
        setOperations.add(key, "h", "l", "l", "e", "o")

        then:
        // Set 자료 구조의 크기를 조회
        def size = setOperations.size(key)
        // Set 자료 구조이므로 중복이 제거됨, size 가 4인지 검증
        size == 4
    }

    // Hash 자료 구조 테스트
    def "RedisTemplate hash operations"() {
        given:
        // Hash 자료 구조를 다루기 위해 HashOperations 인터페이스 인스턴스 생성
        def hashOperations = redisTemplate.opsForHash()
        def key = "hashKey"
        def field = "subKey"
        def value = "value"

        when:
        // Hash 자료 구조에 저장, key 에 field 와 value pair 가 저장됨
        hashOperations.put(key, field, value)

        then:
        // key 와 field 로 value 조회
        def result = hashOperations.get(key, field)
        // result 와 value 가 동일한지 검증
        result == value

        // key 를 이용해서 map 형태로 field 와 value 를 조회
        def entries = hashOperations.entries(key)
        // map 의 key 중에 field 에 해당하는 key 가 있는지 검증
        entries.keySet().containsAll(field)
        // map 의 value 중에 value 에 해당하는 value 있는지 검증
        entries.values().contains(value)

        // Hash 자료 구조의 크기를 조회
        def size = hashOperations.size(key)
        // entries 의 크기와 Hash 자료 구조의 크기가 동일한지 검증
        size == entries.size()
    }
}
