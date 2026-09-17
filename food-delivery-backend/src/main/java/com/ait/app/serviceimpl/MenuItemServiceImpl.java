package com.ait.app.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.MenuCategoryDTO;
import com.ait.app.dto.MenuItemDetailDTO;
import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;
import com.ait.app.dto.MenuItemUpdateDTO;
import com.ait.app.dto.PriceResponseDTO;
import com.ait.app.dto.RestaurantMenuResponseDTO;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceAlreadyExistsException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
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

	@Override
	public MenuItemResponseDTO createMenuItem(int restaurantId, MenuItemRequestDTO requestDTO) {

		java.util.Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
		if (!restaurantOptional.isPresent()) {
			throw new ResourceNotFoundException("Restaurant not found");
		}
		Restaurant restaurant = restaurantOptional.get();

		java.util.Optional<User> adminOptional = userRepository.findById(requestDTO.getAdminId());
		if (!adminOptional.isPresent()) {
			throw new ResourceNotFoundException("Admin not found");
		}
		User admin = adminOptional.get();

		if (admin.getRole() == null || !"PARTNER".equalsIgnoreCase(admin.getRole())) {
			throw new UnauthorizedActionException("User is not a restaurant administrator");
		}

		if (restaurant.getOwner() == null || restaurant.getOwner().getId() != admin.getId()) {
			throw new UnauthorizedActionException("You are not authorized to add menu items to this restaurant");
		}

		if (requestDTO.getPrice() == null || requestDTO.getPrice() <= 0) {
			throw new InvalidRequestException("Price must be greater than 0");
		}

		boolean exists = menuItemRepository.existsByRestaurantIdAndNameIgnoreCaseAndDeletedFalse(
				restaurantId,
				requestDTO.getName());

		if (exists) {
			throw new ResourceAlreadyExistsException(
					"Menu item with name '" + requestDTO.getName() + "' already exists in this restaurant");
		}

		MenuItem menuItem = new MenuItem();
		menuItem.setName(requestDTO.getName());
		menuItem.setDescription(requestDTO.getDescription());
		menuItem.setPrice(requestDTO.getPrice());
		menuItem.setAvailability(requestDTO.getAvailability());
		menuItem.setCategory(requestDTO.getCategory());
		menuItem.setDeleted(false);
		menuItem.setRestaurant(restaurant);

		MenuItem savedItem = menuItemRepository.save(menuItem);

		return new MenuItemResponseDTO(
				savedItem.getId(),
				restaurant.getId(),
				savedItem.getName(),
				savedItem.getDescription(),
				savedItem.getPrice(),
				savedItem.getAvailability(),
				savedItem.getCategory(),
				"Menu item created successfully");
	}

	@Override
	public PriceResponseDTO getItemPrice(int itemId) {
		java.util.Optional<MenuItem> optional = menuItemRepository.findById(itemId);
		if (!optional.isPresent()) {
			throw new ResourceNotFoundException("Menu item not found with id: " + itemId);
		}
		MenuItem menuItem = optional.get();

		if (Boolean.TRUE.equals(menuItem.getDeleted())) {
			throw new ResourceNotFoundException("Menu item not found with id: " + itemId);
		}

		return new PriceResponseDTO(
				menuItem.getId(),
				menuItem.getName(),
				menuItem.getPrice()
		);
	}

	@Override
	public MenuItemResponseDTO updateMenuItem(Integer itemId, MenuItemUpdateDTO updateDTO, Integer adminId) {

		 Optional<MenuItem> optionalMenuItem =menuItemRepository.findById(itemId);
		 
		 if (optionalMenuItem.isEmpty()) {
		        throw new ResourceNotFoundException("Menu item not found with id: " + itemId);
		    }
		 
		 MenuItem menuItem = optionalMenuItem.get();

		 if (Boolean.TRUE.equals(menuItem.getDeleted())) {
			 throw new ResourceNotFoundException("Menu item not found with id: " + itemId);
		 }
		 
		 if (updateDTO.getDescription() != null) {
		        menuItem.setDescription(updateDTO.getDescription());
		    }

		 if (updateDTO.getPrice() != null) {
		        menuItem.setPrice(updateDTO.getPrice());
		    }
		 if (updateDTO.getAvailability() != null) {
		        menuItem.setAvailability(updateDTO.getAvailability());
		    }
		 
		 MenuItem savedItem = menuItemRepository.save(menuItem);

		 return new MenuItemResponseDTO( savedItem.getId(), savedItem.getRestaurant().getId(),savedItem.getName(), savedItem.getDescription(),savedItem.getPrice(), savedItem.getAvailability(),
		            savedItem.getCategory(), "Menu item updated successfully" );
}

	@Override
	public void deleteMenuItem(Integer itemId, Integer adminId) {

		Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(itemId);

		if (optionalMenuItem.isEmpty()) {
			throw new ResourceNotFoundException("Menu item not found with id: " + itemId);
		}

		MenuItem menuItem = optionalMenuItem.get();

		if (Boolean.TRUE.equals(menuItem.getDeleted())) {
			throw new ResourceNotFoundException("Menu item not found with id: " + itemId);
		}

		Restaurant restaurant = menuItem.getRestaurant();

		if (restaurant == null) {
			throw new ResourceNotFoundException("Restaurant not found");
		}

		Optional<User> optionalAdmin = userRepository.findById(adminId);

		if (optionalAdmin.isEmpty()) {
			throw new ResourceNotFoundException("Admin not found");
		}

		User admin = optionalAdmin.get();

		if (admin.getRole() == null || !"PARTNER".equalsIgnoreCase(admin.getRole())) {

			throw new UnauthorizedActionException("User is not a restaurant administrator");
		}

		if (restaurant.getOwner() == null || restaurant.getOwner().getId() != admin.getId()) {

			throw new UnauthorizedActionException("You are not authorized to delete this menu item");
		}

		menuItem.setDeleted(true);

		menuItem.setAvailability(false);

		menuItemRepository.save(menuItem);
	}

	@Override
	public RestaurantMenuResponseDTO getRestaurantMenu(int restaurantId) {

		Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);

		if (restaurantOptional.isEmpty()) {
			throw new ResourceNotFoundException("Restaurant not found");
		}

		List<MenuItem> menuItems = menuItemRepository
				.findByRestaurantIdAndAvailabilityTrueAndDeletedFalse(restaurantId);

		List<String> categories = new ArrayList<>();

		for (MenuItem menuItem : menuItems) {
			String category = menuItem.getCategory();
			if (!categories.contains(category)) {
				categories.add(category);
			}
		}

		List<MenuCategoryDTO> categoryList = new ArrayList<>();

		for (String category : categories) {
			MenuCategoryDTO categoryDTO = new MenuCategoryDTO();
			categoryDTO.setCategory(category);

			List<MenuItemDetailDTO> items = new ArrayList<>();

			for (MenuItem menuItem : menuItems) {
				if (category.equals(menuItem.getCategory())) {
					MenuItemDetailDTO itemDTO = new MenuItemDetailDTO();
					itemDTO.setItemId(menuItem.getId());
					itemDTO.setName(menuItem.getName());
					itemDTO.setDescription(menuItem.getDescription());
					itemDTO.setPrice(menuItem.getPrice());
					items.add(itemDTO);
				}
			}

			categoryDTO.setItems(items);
			categoryList.add(categoryDTO);
		}

		RestaurantMenuResponseDTO response = new RestaurantMenuResponseDTO();
		response.setRestaurantId(restaurantId);
		response.setCategories(categoryList);

		return response;
	}
}
