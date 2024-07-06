package com.example.pharmacyrecommendation.direction.controller;

import com.example.pharmacyrecommendation.direction.entity.Direction;
import com.example.pharmacyrecommendation.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DirectionController {

    // 인코딩된 pk 가 입력되면 pk 를 디코딩해서 엔티티를 조회하는 메서드를 사용하기 위해 의존성 주입
    private final DirectionService directionService;

    // shorten url 을 요청했을 때 길 안내 url 로 리다이렉트하는 메서드
    @GetMapping("/dir/{encodedId}")
    public String searchDirection(@PathVariable("encodedId") String encodedId) {
        // 인코딩된 pk 를 디코딩하고 엔티티를 조회
        String result = directionService.findDirectionUrlById(encodedId);

        log.info("[DirectionController searchDirection] direction url: {}", result);

        // 길 안내 url 로 리다이렉트
        return "redirect:" + result;
    }
}
