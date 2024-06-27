package com.example.pharmacyrecommendation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoApiResponseDto {

    @JsonProperty("meta") // json object mapping
    private MetaDto metaDto;

    @JsonProperty("documents") // json array mapping
    private List<DocumentDto> documentDtoList;
}
