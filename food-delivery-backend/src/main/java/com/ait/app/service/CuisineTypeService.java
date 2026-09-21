package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.CuisineTypeRequestDTO;
import com.ait.app.dto.CuisineTypeResponseDTO;

public interface CuisineTypeService {

	CuisineTypeResponseDTO createCuisineType(CuisineTypeRequestDTO requestDTO);

	List<CuisineTypeResponseDTO> getActiveCuisineTypes();
}