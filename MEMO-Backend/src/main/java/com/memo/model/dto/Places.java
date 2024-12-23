package com.memo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Places {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int placeType;
    private String createdAt;
}
