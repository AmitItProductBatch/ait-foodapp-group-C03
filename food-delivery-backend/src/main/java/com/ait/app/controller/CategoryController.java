package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
}