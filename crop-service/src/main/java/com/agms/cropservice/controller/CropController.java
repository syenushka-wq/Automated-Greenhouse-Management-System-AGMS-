package com.agms.cropservice.controller;

import com.agms.cropservice.Entity.Crop;
import com.agms.cropservice.dto.CropDTO;
import com.agms.cropservice.mapper.CropMapper;
import com.agms.cropservice.repository.CropRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/crops")
public class CropController {

    private final CropRepository cropRepository;

    public CropController(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    // Get all crops
    @GetMapping
    public ResponseEntity<List<CropDTO>> getAllCrops() {
        List<CropDTO> crops = cropRepository.findAll().stream()
                .map(CropMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(crops);
    }

    // Create a new crop
    @PostMapping
    public ResponseEntity<CropDTO> createCrop(@Valid @RequestBody CropDTO cropDTO) {
        Crop crop = CropMapper.toEntity(cropDTO);
        Crop savedCrop = cropRepository.save(crop);
        return ResponseEntity.ok(CropMapper.toDTO(savedCrop));
    }

    // Get a crop by ID
    @GetMapping("/{id}")
    public ResponseEntity<CropDTO> getCropById(@PathVariable Long id) {
        return cropRepository.findById(id)
                .map(CropMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}