package com.example.pharmacyrecommendation.direction.controller;

import com.example.pharmacyrecommendation.direction.dto.InputDto;
import com.example.pharmacyrecommendation.pharmacy.service.PharmacyRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class FormController {

    // 가까운 약국을 찾고 약국 안내(추천)을 저장하는 메서드를 사용하기 위해 의존성 주입
    private final PharmacyRecommendationService pharmacyRecommendationService;

    // 컨트롤러에서 요청이 오면 알맞는 페이지로 이동
    // main 화면으로 이동
    @GetMapping("/")
    public String main() {
        return "main";
    }

    // 약국 안내(추천) 컨트롤러 메서드
    @PostMapping("/search")
    public ModelAndView postDirection(@ModelAttribute InputDto inputDto) {

        // ModelAndView 에 뷰 이름과 데이터를 전달
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("output");
        modelAndView.addObject("outputFormList",
                pharmacyRecommendationService.recommendPharmacyList(inputDto.getAddress()));

        // ModelAndView 객체 반환
        return modelAndView;
    }
}
