package com.ait.app.service;

import com.ait.app.dto.CuisineTypeRequestDTO;
import com.ait.app.dto.CuisineTypeResponseDTO;

public interface CuisineTypeService {

	CuisineTypeResponseDTO createCuisineType(CuisineTypeRequestDTO requestDTO);
}