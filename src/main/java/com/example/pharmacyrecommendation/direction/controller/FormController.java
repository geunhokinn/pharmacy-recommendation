package com.example.pharmacyrecommendation.direction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FormController {

    // 컨트롤러에서 요청이 오면 알맞는 페이지로 이동
    // main 화면으로 이동
    @GetMapping("/")
    public String main() {
        return "main";
    }
}
