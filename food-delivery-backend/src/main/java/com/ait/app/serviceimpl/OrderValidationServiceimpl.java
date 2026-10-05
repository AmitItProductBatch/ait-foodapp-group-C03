package com.ait.app.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.OrderValidationItemDTO;
import com.ait.app.dto.OrderValidationRequestDTO;
import com.ait.app.dto.OrderValidationResponseDTO;
import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.OrderValidationService;
import com.ait.app.service.PriceService;

@Service
public class OrderValidationServiceimpl implements OrderValidationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private PriceService priceService;

	@Override
	public OrderValidationResponseDTO validateOrder(OrderValidationRequestDTO request) {

		List<String> errors = new ArrayList<>();

		if (request == null) {
			errors.add("Request is required");
			return createResponse(errors);
		}

		if (request.getUserId() == null) {
			errors.add("UserId is required");
		}

		if (request.getRestaurantId() == null) {
			errors.add("Restaurant Id is required");
		}

		if (request.getItems() == null || request.getItems().isEmpty()) {

			errors.add("At least one menu item is required");
		}

		if (!errors.isEmpty()) {
			return createResponse(errors);
		}

		validateUser(request.getUserId(), errors);

		Restaurant restaurant = validateRestaurant(request.getRestaurantId(), errors);

		validateItems(request.getItems(), restaurant, request.getRestaurantId(), errors);

		return createResponse(errors);
	}

	private void validateUser(Integer userId, List<String> errors) {

		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isEmpty()) {

			errors.add("User not found with id: " + userId);

			return;
		}

		User user = optionalUser.get();

		if (user.getActive() == null || !user.getActive()) {

			errors.add("User is not active: " + userId);
		}
	}

	private Restaurant validateRestaurant(Integer restaurantId, List<String> errors) {

		Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);

		if (optionalRestaurant.isEmpty()) {

			errors.add("Restaurant not found with id: " + restaurantId);

			return null;
		}

		Restaurant restaurant = optionalRestaurant.get();

		if (restaurant.getActive() == null || !restaurant.getActive()) {

			errors.add("Restaurant is not active: " + restaurantId);
		}

		if (restaurant.getStatus() == null || !"APPROVED".equalsIgnoreCase(restaurant.getStatus())) {

			errors.add("Restaurant is not approved: " + restaurantId);
		}

		return restaurant;
	}

	private void validateItems(List<OrderValidationItemDTO> items, Restaurant restaurant, Integer restaurantId,
			List<String> errors) {

		for (OrderValidationItemDTO item : items) {

			if (item == null) {

				errors.add("Menu item details are required");

				continue;
			}

			if (item.getMenuItemId() == null) {

				errors.add("Menu itemId is required");

				continue;
			}

			if (item.getQuantity() == null) {

				errors.add("Quantity is required for menu item: " + item.getMenuItemId());

			} else if (item.getQuantity() <= 0) {

				errors.add("Quantity must be greater than 0 for menu item: " + item.getMenuItemId());
			}

			if (item.getUnitPrice() == null) {

				errors.add("Unit price is required for menu item: " + item.getMenuItemId());

			} else if (item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {

				errors.add("Unit price cannot be negative for menu item: " + item.getMenuItemId());
			}

			Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(item.getMenuItemId());

			if (optionalMenuItem.isEmpty()) {

				errors.add("Menu item not found with id: " + item.getMenuItemId());

				continue;
			}

			MenuItem menuItem = optionalMenuItem.get();

			validateRestaurantItem(menuItem, restaurant, restaurantId, errors);

			validateAvailability(menuItem, errors);

			validateDeleted(menuItem, errors);

			validatePrice(item, errors);
		}
	}

	private void validateRestaurantItem(MenuItem menuItem, Restaurant restaurant, Integer restaurantId,
			List<String> errors) {

		if (restaurant == null) {
			return;
		}

		if (menuItem.getRestaurant() == null || menuItem.getRestaurant().getId() != restaurant.getId()) {

			errors.add("Menu item " + menuItem.getId() + " does not belong to restaurant " + restaurantId);
		}
	}

	private void validateAvailability(MenuItem menuItem, List<String> errors) {

		if (menuItem.getAvailability() == null || !menuItem.getAvailability()) {

			errors.add("Menu item is not available: " + menuItem.getId());
		}
	}

	private void validateDeleted(MenuItem menuItem, List<String> errors) {

		if (menuItem.getDeleted() != null && menuItem.getDeleted()) {

			errors.add("Menu item is deleted: " + menuItem.getId());
		}
	}

	private void validatePrice(OrderValidationItemDTO item, List<String> errors) {

		if (item.getQuantity() == null || item.getQuantity() <= 0 || item.getUnitPrice() == null
				|| item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {

			return;
		}

		PriceCalculationRequestDTO priceRequest = new PriceCalculationRequestDTO();

		priceRequest.setItemId(item.getMenuItemId());

		priceRequest.setQuantity(item.getQuantity());

		try {

			PriceCalculationResponseDTO priceResponse = priceService.calculatePrice(priceRequest);

			BigDecimal actualPrice = priceResponse.getUnitPrice();

			BigDecimal requestedPrice = item.getUnitPrice();

			if (actualPrice == null || actualPrice.compareTo(requestedPrice) != 0) {

				errors.add("Price mismatch for menu item: " + item.getMenuItemId() + ", Expected price: " + actualPrice
						+ ", received price: " + requestedPrice);
			}

		} catch (RuntimeException ex) {

			errors.add("Price validation failed for menu item: " + item.getMenuItemId());
		}
	}

	private OrderValidationResponseDTO createResponse(List<String> errors) {

		OrderValidationResponseDTO response = new OrderValidationResponseDTO();

		response.setValid(errors.isEmpty());

		response.setErrors(errors);

		return response;
	}
}
