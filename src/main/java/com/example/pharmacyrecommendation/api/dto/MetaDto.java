package com.example.pharmacyrecommendation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetaDto {

    @JsonProperty("total_count") // 스테이크를 카멜로 매핑
    private Integer totalCount; // 검색어에 검색된 문서 수
}
