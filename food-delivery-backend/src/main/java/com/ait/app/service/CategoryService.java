package com.ait.app.service;

import com.ait.app.dto.CategoryRequestDTO;
import com.ait.app.dto.CategoryResponseDTO;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);
}