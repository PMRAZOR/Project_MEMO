package com.memo.places.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int placeType;
    private LocalDateTime createdAt;
}