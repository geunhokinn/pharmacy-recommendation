package com.example.pharmacyrecommendation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    @JsonProperty("address_name") // 스테이크를 카멜로 매핑
    private String addressName; // 주소 이름

    @JsonProperty("y")
    private double latitude; // 위도

    @JsonProperty("X")
    private double longitude; // 경도
}
