package com.ait.app.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;
import com.ait.app.dto.MenuItemUpdateDTO;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
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

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new RuntimeException("Restaurant not found"));

		User admin = userRepository.findById(requestDTO.getAdminId())
				.orElseThrow(() -> new RuntimeException("Admin not found"));

		if (admin.getRole() == null || !"PARTNER".equalsIgnoreCase(admin.getRole())) {
			throw new RuntimeException("User is not a restaurant administrator");
		}

		if (restaurant.getOwner() == null || restaurant.getOwner().getId() != admin.getId()) {
			throw new RuntimeException("You are not authorized to add menu items to this restaurant");
		}

		if (requestDTO.getPrice() == null || requestDTO.getPrice() <= 0) {
			throw new RuntimeException("Price must be greater than 0");
		}

		boolean exists = menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(
				restaurantId,
				requestDTO.getName());

		if (exists) {
			throw new RuntimeException(
					"Menu item with name '" + requestDTO.getName() + "' already exists in this restaurant");
		}

		MenuItem menuItem = new MenuItem();
		menuItem.setName(requestDTO.getName());
		menuItem.setDescription(requestDTO.getDescription());
		menuItem.setPrice(requestDTO.getPrice());
		menuItem.setAvailability(requestDTO.getAvailability());
		menuItem.setCategory(requestDTO.getCategory());
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
	public MenuItemResponseDTO updateMenuItem(Integer itemId, MenuItemUpdateDTO updateDTO, Integer adminId) {

		 Optional<MenuItem> optionalMenuItem =menuItemRepository.findById(itemId);
		 
		 if (optionalMenuItem.isEmpty()) {
		        throw new RuntimeException("Menu item not found with id: " + itemId);
		    }
		 
		 MenuItem menuItem = optionalMenuItem.get();
		 
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
}
