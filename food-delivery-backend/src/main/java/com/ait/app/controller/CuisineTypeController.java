package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.CuisineTypeRequestDTO;
import com.ait.app.dto.CuisineTypeResponseDTO;
import com.ait.app.service.CuisineTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuisine-types")
public class CuisineTypeController {

	@Autowired
	private CuisineTypeService cuisineTypeService;

	@PostMapping
	public ResponseEntity<CuisineTypeResponseDTO> createCuisineType(
			@Valid @RequestBody CuisineTypeRequestDTO requestDTO) {

		CuisineTypeResponseDTO response = cuisineTypeService.createCuisineType(requestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<CuisineTypeResponseDTO>> getActiveCuisineTypes() {
		List<CuisineTypeResponseDTO> response = cuisineTypeService.getActiveCuisineTypes();
		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS))
				.body(response);
	}
}