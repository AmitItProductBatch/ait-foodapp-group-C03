package com.ait.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ait.app.dto.CategoryRequestDTO;
import com.ait.app.dto.CategoryResponseDTO;
import com.ait.app.entity.Category;
import com.ait.app.exception.ResourceAlreadyExistsException;
import com.ait.app.repository.CategoryRepository;
import com.ait.app.serviceimpl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	private CategoryRequestDTO requestDTO;

	@BeforeEach
	void setUp() {

		requestDTO = new CategoryRequestDTO();

		requestDTO.setName("Appetizers");
		requestDTO.setDescription("Light dishes");
	}

	@Test
	void shouldCreateCategorySuccessfully() {

		when(categoryRepository.existsByNameIgnoreCase("appetizers")).thenReturn(false);

		Category savedCategory = new Category();

		savedCategory.setId(1);
		savedCategory.setName("appetizers");
		savedCategory.setDescription("Light dishes");

		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

		CategoryResponseDTO response = categoryService.createCategory(requestDTO);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1);
		assertThat(response.getName()).isEqualTo("appetizers");
		assertThat(response.getDescription()).isEqualTo("Light dishes");

		verify(categoryRepository).existsByNameIgnoreCase("appetizers");

		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	void shouldThrowExceptionWhenCategoryAlreadyExists() {

		when(categoryRepository.existsByNameIgnoreCase("appetizers")).thenReturn(true);

		assertThatThrownBy(() -> categoryService.createCategory(requestDTO))
				.isInstanceOf(ResourceAlreadyExistsException.class).hasMessageContaining("already exists");

		verify(categoryRepository, never()).save(any(Category.class));
	}
}