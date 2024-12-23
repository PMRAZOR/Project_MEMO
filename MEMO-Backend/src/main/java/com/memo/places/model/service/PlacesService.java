package com.memo.places.model.service;

import com.memo.places.model.dto.PlaceTypes;
import com.memo.places.model.dto.Places;

import java.util.List;

public interface PlacesService {
    public int insertPlaces(Places places);
    public List<PlaceTypes> getTypeList();
    public List<Places> selectByKeyword(String keyword);
}
