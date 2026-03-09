package com.agms.cropservice.service;

import com.agms.cropservice.Entity.Crop;
import com.agms.cropservice.dto.CropDTO;
import com.agms.cropservice.mapper.CropMapper;
import com.agms.cropservice.repository.CropRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropService {

    private final CropRepository cropRepository;

    public CropService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public List<CropDTO> getAllCrops() {
        return cropRepository.findAll()
                .stream()
                .map(CropMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CropDTO createCrop(CropDTO cropDTO) {
        // Convert DTO → Entity
        Crop crop = CropMapper.toEntity(cropDTO);
        // Save entity
        Crop savedCrop = cropRepository.save(crop);
        // Convert back to DTO
        return CropMapper.toDTO(savedCrop);
    }
}