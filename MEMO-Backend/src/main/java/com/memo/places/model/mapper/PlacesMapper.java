package com.memo.places.model.mapper;

import com.memo.places.model.dto.PlaceTypes;
import com.memo.places.model.dto.Places;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlacesMapper {
    public int insertPlaces(Places places);
    public List<PlaceTypes> getTypeList();
    public List<Places> selectByKeyword(String keyword);
}
