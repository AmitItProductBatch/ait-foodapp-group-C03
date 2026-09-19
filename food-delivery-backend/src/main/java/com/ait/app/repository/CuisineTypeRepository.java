package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.CuisineType;

public interface CuisineTypeRepository extends JpaRepository<CuisineType, Long> {

	Optional<CuisineType> findByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCase(String name);
}