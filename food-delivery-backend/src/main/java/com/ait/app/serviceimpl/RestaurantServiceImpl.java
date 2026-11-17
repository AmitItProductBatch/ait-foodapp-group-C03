
package com.ait.app.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantDetailsDTO;
import com.ait.app.dto.RestaurantListResponseDTO;
import com.ait.app.dto.RestaurantRequestDTO;
import com.ait.app.dto.RestaurantResponseDTO;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
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
			throw new InvalidRequestException("Owner ID is required");
		}

		int ownerId = requestDTO.getOwnerId();

		Optional<User> optionalUser = userRepository.findById(ownerId);

		User owner;

		if (optionalUser.isPresent()) {
			owner = optionalUser.get();
		} else {
			throw new ResourceNotFoundException("Owner not found");
		}

		if (owner.getRole() == null || !"PARTNER".equalsIgnoreCase(owner.getRole())) {

			throw new UnauthorizedActionException("Owner must have PARTNER role");
		}

		if (!Boolean.TRUE.equals(owner.getActive())) {

			throw new InvalidRequestException("Owner account is not active");
		}

		Restaurant restaurant = new Restaurant();

		restaurant.setName(requestDTO.getName());
		restaurant.setAddress(requestDTO.getAddress());
		restaurant.setCuisine(requestDTO.getCuisine());
		restaurant.setContact(requestDTO.getContact());

		restaurant.setHours(requestDTO.getHours());

		restaurant.setOwner(owner);

		restaurant.setStatus("PENDING");
		restaurant.setActive(false);

		Restaurant savedRestaurant = restaurantRepository.save(restaurant);

		return new RestaurantResponseDTO(savedRestaurant.getId(), owner.getId(), "Restaurant created successfully",
				savedRestaurant.getStatus());
	}

	@Override
	public RestaurantDetailsDTO getRestaurantDetails(Integer id) {

		Optional<Restaurant> optionalRestaurant = restaurantRepository.findByIdAndActiveTrue(id);

		if (optionalRestaurant.isEmpty()) {
			throw new ResourceNotFoundException("Restaurant not found or inactive");
		}

		Restaurant restaurant = optionalRestaurant.get();

		RestaurantDetailsDTO dto = new RestaurantDetailsDTO();

		dto.setRestaurantId(restaurant.getId());
		dto.setName(restaurant.getName());
		dto.setAddress(restaurant.getAddress());
		dto.setHours(restaurant.getHours());
		dto.setCuisine(restaurant.getCuisine());

		// Rating tumhi feedback table madun ghya foreign key use kara ithya maunally
		// takta yatye using postmanetc..
		dto.setRating(0.0);

		return dto;
	}

	public List<RestaurantListResponseDTO> getAllRestaurants() {

		// TODO Auto-generated method stub

		List<Restaurant> restaurants = restaurantRepository.findAll().stream()
				.filter(r -> Boolean.TRUE.equals(r.getActive())).collect(java.util.stream.Collectors.toList());

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
		List<Restaurant> restaurants = restaurantRepository.findRestaurantsByCuisine(cuisine).stream()
				.filter(r -> Boolean.TRUE.equals(r.getActive())).collect(java.util.stream.Collectors.toList());
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

	@Autowired
	private com.ait.app.repository.MenuItemRepository menuItemRepository;

	@Override
	public com.ait.app.dto.RestaurantMenuResponseDTO getRestaurantMenu(Integer restaurantId) {

		restaurantRepository.findByIdAndActiveTrue(restaurantId)
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found or inactive"));

		java.util.Map<String, com.ait.app.dto.MenuCategoryDTO> grouped = new java.util.LinkedHashMap<>();

		for (com.ait.app.entity.MenuItem item : menuItemRepository
				.findByRestaurantIdAndAvailabilityTrueAndDeletedFalse(restaurantId)) {

			String categoryName = item.getCategory() != null ? item.getCategory().getName() : "other";

			com.ait.app.dto.MenuCategoryDTO category = grouped.computeIfAbsent(categoryName, name -> {
				com.ait.app.dto.MenuCategoryDTO c = new com.ait.app.dto.MenuCategoryDTO();
				c.setCategory(name);
				return c;
			});

			com.ait.app.dto.MenuItemDetailDTO detail = new com.ait.app.dto.MenuItemDetailDTO();
			detail.setItemId(item.getId());
			detail.setName(item.getName());
			detail.setDescription(item.getDescription());
			detail.setPrice(item.getPrice());

			category.getItems().add(detail);
		}

		com.ait.app.dto.RestaurantMenuResponseDTO response = new com.ait.app.dto.RestaurantMenuResponseDTO();
		response.setRestaurantId(restaurantId);
		response.setCategories(new ArrayList<>(grouped.values()));

		return response;
	}

	/**
	 * Full menu (including unavailable items) for the restaurant's owner or an
	 * admin.
	 */
	@Override
	public List<com.ait.app.dto.MenuItemResponseDTO> getManagedMenu(Integer restaurantId, Integer userId,
			String userRole) {

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

		boolean admin = "ADMIN".equalsIgnoreCase(userRole);
		boolean owner = restaurant.getOwner() != null && restaurant.getOwner().getId() == userId;

		if (!admin && !owner) {
			throw new UnauthorizedActionException("You are not authorized to manage this restaurant");
		}

		List<com.ait.app.dto.MenuItemResponseDTO> result = new ArrayList<>();

		for (com.ait.app.entity.MenuItem item : menuItemRepository.findByRestaurantIdAndDeletedFalse(restaurantId)) {

			result.add(new com.ait.app.dto.MenuItemResponseDTO(item.getId(), restaurantId, item.getName(),
					item.getDescription(), item.getPrice(), item.getAvailability(),
					item.getCategory() != null ? item.getCategory().getName() : null, null));
		}

		return result;
	}

	@Override
	public List<com.ait.app.dto.RestaurantManageDTO> getManagedRestaurants(Integer userId, String userRole) {

		boolean admin = "ADMIN".equalsIgnoreCase(userRole);

		List<com.ait.app.dto.RestaurantManageDTO> result = new ArrayList<>();

		for (Restaurant restaurant : restaurantRepository.findAll()) {

			boolean owner = restaurant.getOwner() != null && restaurant.getOwner().getId() == userId;

			if (admin || owner) {
				result.add(toManageDto(restaurant));
			}
		}

		return result;
	}

	@Override
	public com.ait.app.dto.RestaurantManageDTO updateApproval(Integer restaurantId, boolean approve) {

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

		restaurant.setStatus(approve ? "APPROVED" : "REJECTED");
		restaurant.setActive(approve);

		return toManageDto(restaurantRepository.save(restaurant));
	}

	private com.ait.app.dto.RestaurantManageDTO toManageDto(Restaurant restaurant) {

		com.ait.app.dto.RestaurantManageDTO dto = new com.ait.app.dto.RestaurantManageDTO();

		dto.setRestaurantId(restaurant.getId());
		dto.setName(restaurant.getName());
		dto.setAddress(restaurant.getAddress());
		dto.setCuisine(restaurant.getCuisine());
		dto.setContact(restaurant.getContact());
		dto.setHours(restaurant.getHours());
		dto.setStatus(restaurant.getStatus());
		dto.setActive(Boolean.TRUE.equals(restaurant.getActive()));
		dto.setOwnerId(restaurant.getOwner() != null ? restaurant.getOwner().getId() : 0);
		dto.setRating(restaurant.getRating());

		return dto;
	}
}
