package com.memo.places.controllers;

import com.memo.model.dto.Visits;
import com.memo.places.model.dto.PlaceTypes;
import com.memo.places.model.dto.Places;
import com.memo.places.model.service.PlacesService;
import com.memo.posts.model.dto.PostWithVisits;
import com.memo.posts.model.dto.Posts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlacesControllers {
    private final PlacesService placesService;

    @GetMapping("/types")
    public ResponseEntity<?> getTypes() {
        List<PlaceTypes> list = placesService.getTypeList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> insertPlace(@RequestBody Places places) {
        System.out.println("받은 장소 데이터 : " + places);
        int cnt = placesService.insertPlaces(places);
        if (cnt != 0) return ResponseEntity.status(201).build();
        return ResponseEntity.status(404).body("Posts Insert Failed");
    }

    @GetMapping
    public ResponseEntity<?> selectByKeyword(@RequestParam(required = false) String keyword) {
        System.out.println(keyword);
        List<Places> list = placesService.selectByKeyword(keyword);
        if (list != null) return ResponseEntity.ok(list);
        return ResponseEntity.status(404).body("Get List Query Failed");
    }
}
