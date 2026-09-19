package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CuisineTypeRequestDTO;
import com.ait.app.dto.CuisineTypeResponseDTO;
import com.ait.app.entity.CuisineType;
import com.ait.app.exception.ResourceAlreadyExistsException;
import com.ait.app.repository.CuisineTypeRepository;
import com.ait.app.service.CuisineTypeService;

@Service
public class CuisineTypeServiceImpl implements CuisineTypeService {

	@Autowired
	private CuisineTypeRepository cuisineTypeRepository;

	@Override
	public CuisineTypeResponseDTO createCuisineType(CuisineTypeRequestDTO requestDTO) {

		String name = requestDTO.getName().trim();

		if (cuisineTypeRepository.existsByNameIgnoreCase(name)) {
			throw new ResourceAlreadyExistsException("Cuisine type already exists: " + name);
		}

		CuisineType cuisineType = new CuisineType();

		cuisineType.setName(name);
		cuisineType.setDescription(requestDTO.getDescription().trim());

		CuisineType savedCuisineType = cuisineTypeRepository.save(cuisineType);

		return new CuisineTypeResponseDTO(savedCuisineType.getId(), savedCuisineType.getName(),
				savedCuisineType.getDescription());
	}
}