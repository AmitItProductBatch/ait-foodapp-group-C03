package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantRequestDTO;
import com.ait.app.dto.RestaurantResponseDTO;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.RestaurantService;

@Service
public class RestaurantServiceImpl implements RestaurantService {
	@Autowired
	RestaurantRepository restaurantRepository;
	@Autowired
	UserRepository userRepository;

	@Override
	public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO requestDTO) {

		User owner = userRepository.findById(requestDTO.getOwnerId()).orElseThrow(() -> new RuntimeException("Owner not found"));

		if (!"PARTNER".equalsIgnoreCase(owner.getRole())) {

			throw new RuntimeException("Owner must have PARTNER role");
		}

		if (!owner.isActive()) {

			throw new RuntimeException("Owner account is not active");
		}

		Restaurant restaurant = new Restaurant();

		restaurant.setName(requestDTO.getName());

		restaurant.setAddress(requestDTO.getAddress());

		restaurant.setCuisine(requestDTO.getCuisine());

		restaurant.setContact(requestDTO.getContact());

		restaurant.setOwner(owner);

		restaurant.setStatus("PENDING");

		Restaurant savedRestaurant = restaurantRepository.save(restaurant);

		return new RestaurantResponseDTO(savedRestaurant.getId(), owner.getId(), "Restaurant created successfully",savedRestaurant.getStatus());
	}
}