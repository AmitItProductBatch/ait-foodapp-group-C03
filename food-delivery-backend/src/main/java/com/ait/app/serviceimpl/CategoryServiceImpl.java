package com.ait.app.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.CategoryRequestDTO;
import com.ait.app.dto.CategoryResponseDTO;
import com.ait.app.entity.Category;
import com.ait.app.entity.User;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceAlreadyExistsException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.CategoryRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

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

	@Override
	public List<CategoryResponseDTO> getActiveCategories() {

		List<Category> categories = categoryRepository.findAllByOrderByNameAsc();

		List<CategoryResponseDTO> response = new ArrayList<>();

		for (Category category : categories) {

			CategoryResponseDTO dto = new CategoryResponseDTO();

			dto.setId(category.getId());
			dto.setName(category.getName());
			dto.setDescription(category.getDescription());

			response.add(dto);
		}

		return response;
	}

	@Override
	public void deactivateCategory(Integer categoryId) {
		Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
		if (optionalCategory.isEmpty()) {
			throw new ResourceNotFoundException("Category", categoryId);
		}
		Category category = optionalCategory.get();
		category.setActive(false);
		categoryRepository.save(category);
	}

	@Override
	@Transactional
	public CategoryResponseDTO updateCategory(int categoryId, int adminId, CategoryRequestDTO requestDTO) {

		Optional<User> adminOptional = userRepository.findById(adminId);

		if (!adminOptional.isPresent()) {
			throw new ResourceNotFoundException("Admin not found with id: " + adminId);
		}

		User admin = adminOptional.get();

		if (admin.getRole() == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
			throw new InvalidRequestException("Only admin can update category");
		}

		Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

		if (!categoryOptional.isPresent()) {
			throw new ResourceNotFoundException("Category not found with id: " + categoryId);
		}

		Category category = categoryOptional.get();

		String newName = requestDTO.getName().trim().toLowerCase();

		boolean exists = categoryRepository.existsByNameIgnoreCaseAndIdNot(newName, categoryId);

		if (exists) {
			throw new ResourceAlreadyExistsException(
					"Category with name '" + requestDTO.getName() + "' already exists");
		}

		category.setName(newName);

		if (requestDTO.getDescription() != null) {
			category.setDescription(requestDTO.getDescription().trim());
		}

		Category updatedCategory = categoryRepository.save(category);

		CategoryResponseDTO response = new CategoryResponseDTO();

		response.setId(updatedCategory.getId());
		response.setName(updatedCategory.getName());
		response.setDescription(updatedCategory.getDescription());

		return response;
	}
}
