package com.memo.places.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceRequest {
    @NotBlank(message = "장소 이름은 필수입니다")
    private String name;

    private String address;

    @NotNull(message = "위도는 필수입니다")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다")
    private Double longitude;

    @NotNull(message = "장소 타입은 필수입니다")
    private Integer placeType;
}