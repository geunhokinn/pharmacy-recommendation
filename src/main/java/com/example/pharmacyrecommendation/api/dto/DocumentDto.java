package com.example.pharmacyrecommendation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    @JsonProperty("place_name") // 카테고리로 장소 검색 api 응답 추가
    private String placeName; // 약국 이름

    @JsonProperty("address_name") // 스테이크를 카멜로 매핑
    private String addressName; // 주소 이름

    @JsonProperty("y")
    private double latitude; // 위도

    @JsonProperty("x")
    private double longitude; // 경도

    @JsonProperty("distance") // 카테고리로 장소 검색 api 응답 추가
    private double distance; // 거리
}
