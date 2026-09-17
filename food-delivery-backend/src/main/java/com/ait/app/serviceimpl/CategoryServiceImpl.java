package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.CategoryRequestDTO;
import com.ait.app.dto.CategoryResponseDTO;
import com.ait.app.entity.Category;
import com.ait.app.exception.ResourceAlreadyExistsException;
import com.ait.app.repository.CategoryRepository;
import com.ait.app.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	@Transactional
	public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {

		String normalizedName = requestDTO.getName().trim().toLowerCase();

		if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
			throw new ResourceAlreadyExistsException(
					"Category with name '" + requestDTO.getName() + "' already exists");
		}

		Category category = new Category();
		category.setName(normalizedName);

		if (requestDTO.getDescription() != null) {
			category.setDescription(requestDTO.getDescription().trim());
		}

		Category savedCategory = categoryRepository.save(category);

		return new CategoryResponseDTO(savedCategory.getId(), savedCategory.getName(), savedCategory.getDescription());
	}
}