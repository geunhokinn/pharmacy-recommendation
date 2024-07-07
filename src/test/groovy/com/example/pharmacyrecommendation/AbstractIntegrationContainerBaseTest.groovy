package com.example.pharmacyrecommendation

import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification


// singleton container 를 만들기 위한 abstract class 선언

@SpringBootTest // 스프링 부트 통합 테스트, spock test code 작성
abstract class AbstractIntegrationContainerBaseTest extends Specification {

    // redis 를 사용하기 위한 genericContainer 설정
    static final GenericContainer MY_REDIS_CONTAINER

    static {
        // expose port 는 도커에서 expose 한 port 이다.
        // host post 는 테스트 컨테이너가 충돌되지 않는 port 를 생성해서 도커 port 와 mapping 한다.
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")
                .withExposedPorts(6379)

        // redis container 시작(랜덤한 host port 를 mapping)
        MY_REDIS_CONTAINER.start()

        // spring boot 는 redis 와 통신을 하기 위해 mapping 된 host 와 port 를 알아야 한다.
        // 랜덤한 host port 를 spring boot 에게 알려주기
        System.setProperty("spring.data.redis.host", MY_REDIS_CONTAINER.getHost())
        // docker port 6379와 mapping 된 port 를 spring boot 에게 알려주기, string 값이 들어가야 됨
        System.setProperty("spring.data.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString())
    }
}
