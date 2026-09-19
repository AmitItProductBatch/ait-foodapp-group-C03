package com.ait.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	Optional<Category> findByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCase(String name);

	List<Category> findByActiveTrueOrderByNameAsc();
	
	boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
}