package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.CategoryRequestDTO;
import com.ait.app.dto.CategoryResponseDTO;
import com.ait.app.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {

		CategoryResponseDTO response = categoryService.createCategory(requestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<CategoryResponseDTO>> getCategories() {

		List<CategoryResponseDTO> response = categoryService.getActiveCategories();

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS)).body(response);
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable int categoryId, @RequestParam int adminId,
			@Valid @RequestBody CategoryRequestDTO requestDTO) {

		CategoryResponseDTO response = categoryService.updateCategory(categoryId, adminId, requestDTO);

		return ResponseEntity.ok(response);
	}
}