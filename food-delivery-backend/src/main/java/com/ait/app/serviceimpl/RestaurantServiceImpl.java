
package com.ait.app.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantListResponseDTO;
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
	private RestaurantRepository restaurantRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO requestDTO) {

		if (requestDTO.getOwnerId() == null) {
			throw new RuntimeException("Owner ID is required");
		}

		User owner = userRepository.findById(requestDTO.getOwnerId())
				.orElseThrow(() -> new RuntimeException("Owner not found"));

		if (owner.getRole() == null || !"PARTNER".equalsIgnoreCase(owner.getRole())) {

			throw new RuntimeException("Owner must have PARTNER role");
		}

		if (!Boolean.TRUE.equals(owner.getActive())) {

			throw new RuntimeException("Owner account is not active");
		}

		Restaurant restaurant = new Restaurant();

		restaurant.setName(requestDTO.getName());
		restaurant.setAddress(requestDTO.getAddress());
		restaurant.setCuisine(requestDTO.getCuisine());
		restaurant.setContact(requestDTO.getContact());

		restaurant.setOwner(owner);

		restaurant.setStatus("PENDING");
		restaurant.setActive(false);

		Restaurant savedRestaurant = restaurantRepository.save(restaurant);

		return new RestaurantResponseDTO(savedRestaurant.getId(), owner.getId(), "Restaurant created successfully",
				savedRestaurant.getStatus());
	}

	public List<RestaurantListResponseDTO> getAllRestaurants() {

		// TODO Auto-generated method stub

		List<Restaurant> restaurants = restaurantRepository.findAll();

		List<RestaurantListResponseDTO> response = new ArrayList<>();

		for (Restaurant restaurant : restaurants) {
			RestaurantListResponseDTO dto = new RestaurantListResponseDTO();

			dto.setRestaurantId(restaurant.getId());
			dto.setName(restaurant.getName());
			dto.setAddress(restaurant.getAddress());
			dto.setContact(restaurant.getContact());
			dto.setCuisine(restaurant.getCuisine());
			dto.setRating(restaurant.getRating());
			response.add(dto);
		}

		return response;
	}

	public List<RestaurantListResponseDTO> getRestaurantsByCuisine(String cuisine) {
		// TODO Auto-generated method stub
		List<Restaurant> restaurants = restaurantRepository.findRestaurantsByCuisine(cuisine);
		List<RestaurantListResponseDTO> response = new ArrayList<>();

		for (Restaurant restaurant : restaurants) {
			RestaurantListResponseDTO dto = new RestaurantListResponseDTO();

			dto.setRestaurantId(restaurant.getId());
			dto.setName(restaurant.getName());
			dto.setAddress(restaurant.getAddress());
			dto.setContact(restaurant.getContact());
			dto.setCuisine(restaurant.getCuisine());
			dto.setRating(restaurant.getRating());
			response.add(dto);
		}

		return response;
	}
}
