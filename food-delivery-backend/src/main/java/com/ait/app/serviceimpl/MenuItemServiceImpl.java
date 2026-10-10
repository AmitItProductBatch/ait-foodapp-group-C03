package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;
import com.ait.app.dto.MenuItemUpdateDTO;
import com.ait.app.dto.PriceResponseDTO;
import com.ait.app.entity.Category;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceAlreadyExistsException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
import com.ait.app.repository.CategoryRepository;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.MenuItemService;

@Service
public class MenuItemServiceImpl implements MenuItemService {

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	@Transactional
	public MenuItemResponseDTO createMenuItem(int restaurantId, MenuItemRequestDTO requestDTO) {

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

		User admin = userRepository.findById(requestDTO.getAdminId()).orElseThrow(
				() -> new ResourceNotFoundException("Admin not found with id: " + requestDTO.getAdminId()));

		if (admin.getRole() == null || !"PARTNER".equalsIgnoreCase(admin.getRole())) {
			throw new UnauthorizedActionException("Only restaurant partners can add menu items");
		}

		if (restaurant.getOwner() == null || restaurant.getOwner().getId() != admin.getId()) {
			throw new UnauthorizedActionException("You are not authorized to add menu items to this restaurant");
		}

		if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
			throw new InvalidRequestException("Menu item name is required");
		}

		if (requestDTO.getPrice() == null || requestDTO.getPrice() <= 0) {
			throw new InvalidRequestException("Price must be greater than 0");
		}

		boolean exists = menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(restaurantId,
				requestDTO.getName().trim());

		if (exists) {
			throw new ResourceAlreadyExistsException("Menu item already exists in this restaurant");
		}

		Category category = categoryRepository.findById(requestDTO.getCategoryId()).orElseThrow(
				() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

		MenuItem menuItem = new MenuItem();

		menuItem.setName(requestDTO.getName().trim());
		menuItem.setDescription(requestDTO.getDescription());
		menuItem.setPrice(requestDTO.getPrice());
		menuItem.setAvailability(requestDTO.getAvailability());
		menuItem.setCategory(category);
		menuItem.setRestaurant(restaurant);
		menuItem.setDeleted(false);

		MenuItem savedItem = menuItemRepository.save(menuItem);

		return new MenuItemResponseDTO(savedItem.getId(), savedItem.getRestaurant().getId(), savedItem.getName(),
				savedItem.getDescription(), savedItem.getPrice(), savedItem.getAvailability(),
				savedItem.getCategory().getName(), "Menu item created successfully");
	}

	@Override
	@Transactional
	public MenuItemResponseDTO updateMenuItem(Integer itemId, MenuItemUpdateDTO updateDTO, Integer adminId) {

		MenuItem menuItem = menuItemRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemId));

		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));

		if (admin.getRole() == null || !"PARTNER".equalsIgnoreCase(admin.getRole())) {
			throw new UnauthorizedActionException("Only restaurant partners can update menu items");
		}

		Restaurant restaurant = menuItem.getRestaurant();

		if (restaurant == null || restaurant.getOwner() == null || restaurant.getOwner().getId() != admin.getId()) {
			throw new UnauthorizedActionException("You are not authorized to update this menu item");
		}

		if (updateDTO.getDescription() != null) {
			menuItem.setDescription(updateDTO.getDescription());
		}

		if (updateDTO.getPrice() != null) {
			if (updateDTO.getPrice() <= 0) {
				throw new InvalidRequestException("Price must be greater than 0");
			}

			menuItem.setPrice(updateDTO.getPrice());
		}

		if (updateDTO.getAvailability() != null) {
			menuItem.setAvailability(updateDTO.getAvailability());
		}

		MenuItem savedItem = menuItemRepository.save(menuItem);

		return new MenuItemResponseDTO(savedItem.getId(), savedItem.getRestaurant().getId(), savedItem.getName(),
				savedItem.getDescription(), savedItem.getPrice(), savedItem.getAvailability(),
				savedItem.getCategory() != null ? savedItem.getCategory().getName() : null,
				"Menu item updated successfully");
	}

	@Override
	@Transactional
	public boolean deleteMenuItem(Integer itemId, Integer adminId) {

		MenuItem menuItem = menuItemRepository.findById(itemId).orElse(null);

		if (menuItem == null) {
			return false;
		}

		User admin = userRepository.findById(adminId).orElse(null);

		if (admin == null || admin.getRole() == null || !"PARTNER".equalsIgnoreCase(admin.getRole())) {
			return false;
		}

		Restaurant restaurant = menuItem.getRestaurant();

		if (restaurant == null || restaurant.getOwner() == null || restaurant.getOwner().getId() != admin.getId()) {
			return false;
		}

		menuItemRepository.delete(menuItem);

		return true;
	}

	@Override
	public PriceResponseDTO getItemPrice(int itemId) {

		MenuItem menuItem = menuItemRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemId));

		if (Boolean.TRUE.equals(menuItem.getDeleted())) {
			throw new ResourceNotFoundException("Menu item is not available");
		}

		PriceResponseDTO response = new PriceResponseDTO();
		response.setItemId(menuItem.getId());
		response.setPrice(menuItem.getPrice());
		return response;
	}
}
