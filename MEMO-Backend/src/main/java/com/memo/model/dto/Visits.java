package com.memo.model.dto;

import com.memo.photos.model.dto.Photos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Visits {
    private int id;
    private int postId;
    private int placeId;
    private String visitDate;
    private String title;
    private String description;
    private String createdAt;
    // 추가된 필드들
    private String placeName;
    private String placeAddress;
    private double placeLatitude;
    private double placeLongitude;
    private int placeType;
    private List<Photos> photos;
    private int photoCount;
}