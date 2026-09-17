package com.ait.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ait.app.entity.Category;
import com.ait.app.repository.CategoryRepository;

@SpringBootTest
class CategoryRepositoryIntegrationTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	void shouldPersistCategory() {

		Category category = new Category();

		category.setName("Repository Appetizers");
		category.setDescription("Light dishes");

		Category savedCategory = categoryRepository.saveAndFlush(category);

		assertThat(savedCategory.getId()).isNotNull();
		assertThat(savedCategory.getName()).isEqualTo("Repository Appetizers");
		assertThat(savedCategory.getDescription()).isEqualTo("Light dishes");

		Optional<Category> fetchedCategory = categoryRepository.findById(savedCategory.getId());

		assertThat(fetchedCategory).isPresent();
		assertThat(fetchedCategory.get().getName()).isEqualTo("Repository Appetizers");
	}

	@Test
	void shouldCheckCategoryNameIgnoringCase() {

		Category category = new Category();

		category.setName("Unique Beverages");
		category.setDescription("Drinks");

		categoryRepository.saveAndFlush(category);

		boolean exists = categoryRepository.existsByNameIgnoreCase("unique beverages");

		assertThat(exists).isTrue();
	}
}