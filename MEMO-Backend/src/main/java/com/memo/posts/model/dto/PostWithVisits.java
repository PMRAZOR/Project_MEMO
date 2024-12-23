package com.memo.posts.model.dto;

import com.memo.model.dto.Places;
import com.memo.model.dto.Visits;
import com.memo.photos.model.dto.Photos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PostWithVisits {
    private Posts post;
    private List<Visit> visits;

    // private 생성자
    private PostWithVisits(Posts post, List<Visit> visits) {
        this.post = post;
        this.visits = visits;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Visit {
        private String title;
        private String description;
        private String visitDate;
        private Place place;
        private List<Photos> photos;

        public Visit(String title, String description, String visitDate, Place place, List<Photos> photos) {
            this.title = title;
            this.description = description;
            this.visitDate = visitDate;
            this.place = place;
            this.photos = photos != null ? photos : new ArrayList<>();  // 전달받은 photos 사용
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Place {
        private int id;
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private String placeType;

        public Place(int id, String name, String address, double latitude, double longitude, int placeType) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeType = String.valueOf(placeType); // int를 String으로 변환
        }
    }

    // Visits 리스트로부터 PostWithVisits 객체를 생성하는 팩토리 메소드
    public static PostWithVisits createFromVisits(Posts post, List<Visits> visits) {
        List<Visit> transformedVisits = visits.stream()
                .map(visit -> {
                    Place place = new Place(
                            visit.getPlaceId(),
                            visit.getPlaceName(),
                            visit.getPlaceAddress(),
                            visit.getPlaceLatitude(),
                            visit.getPlaceLongitude(),
                            visit.getPlaceType()
                    );
                    return new Visit(visit.getTitle(), visit.getDescription(), visit.getVisitDate(), place, visit.getPhotos());
                })
                .collect(Collectors.toList());
        return new PostWithVisits(post, transformedVisits);
    }

    // VisitWithPlace 리스트로부터 PostWithVisits 객체를 생성하는 팩토리 메소드
    public static PostWithVisits createFromVisitWithPlaces(Posts post, List<VisitWithPlace> visitWithPlaces) {
        List<Visit> transformedVisits = visitWithPlaces.stream()
                .map(vwp -> {
                    Visits visit = vwp.getVisit();
                    Places place = vwp.getPlace();
                    Place newPlace = new Place(
                            place.getId(),
                            place.getName(),
                            place.getAddress(),
                            place.getLatitude(),
                            place.getLongitude(),
                            place.getPlaceType()
                    );
                    return new Visit(visit.getTitle(), visit.getDescription(), visit.getVisitDate(), newPlace, visit.getPhotos());
                })
                .collect(Collectors.toList());
        return new PostWithVisits(post, transformedVisits);
    }

    // 이전 버전과의 호환성을 위해 유지
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class VisitWithPlace {
        private Visits visit;
        private Places place;

        public VisitWithPlace(Visits visit, Places place) {
            this.visit = visit;
            this.place = place;
        }
    }
}
