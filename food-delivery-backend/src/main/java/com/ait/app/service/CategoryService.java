package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.CategoryRequestDTO;
import com.ait.app.dto.CategoryResponseDTO;

public interface CategoryService {

	CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);

	List<CategoryResponseDTO> getActiveCategories();

	void deactivateCategory(Integer categoryId);
}