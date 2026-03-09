package com.agms.cropservice.mapper;

import com.agms.cropservice.Entity.Crop;
import com.agms.cropservice.dto.CropDTO;

public class CropMapper {

    // Convert Entity -> DTO
    public static CropDTO toDTO(Crop crop) {
        if (crop == null) return null;
        return new CropDTO(crop.getId(), crop.getName(), crop.getType());
    }

    // Convert DTO -> Entity
    public static Crop toEntity(CropDTO dto) {
        if (dto == null) return null;
        return new Crop(dto.getId(), dto.getName(), dto.getType());
    }
}