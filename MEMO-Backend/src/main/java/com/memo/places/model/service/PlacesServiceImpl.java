package com.memo.places.model.service;

import com.memo.places.model.dto.PlaceTypes;
import com.memo.places.model.dto.Places;
import com.memo.places.model.mapper.PlacesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class PlacesServiceImpl implements PlacesService{
    private final PlacesMapper placesMapper;
    @Override
    public int insertPlaces(Places places) {
        return placesMapper.insertPlaces(places);
    }

    @Override
    public List<PlaceTypes> getTypeList() {
        return placesMapper.getTypeList();
    }

    @Override
    public List<Places> selectByKeyword(String keyword) {
        return placesMapper.selectByKeyword(keyword);
    }
}
