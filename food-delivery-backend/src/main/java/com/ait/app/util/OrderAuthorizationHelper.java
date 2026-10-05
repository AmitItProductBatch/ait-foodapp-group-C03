package com.ait.app.util;

import com.ait.app.entity.Order;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.UnauthorizedActionException;
import com.ait.app.repository.RestaurantRepository;

public class OrderAuthorizationHelper {

	private static final String ADMIN_ROLE = "ADMIN";

	public static void verifyRestaurantOrAdminAuthorization(Order order, Integer userId, String userRole,
			RestaurantRepository restaurantRepository) {
		if (isAdmin(userRole)) {
			return;
		}

		if (!isRestaurantOwner(order, userId, restaurantRepository)) {
			throw new UnauthorizedActionException(
					"User is not authorized to update this order. Only restaurant owner or admin can update order status.");
		}
	}

	private static boolean isAdmin(String userRole) {
		return ADMIN_ROLE.equalsIgnoreCase(userRole);
	}

	private static boolean isRestaurantOwner(Order order, Integer userId, RestaurantRepository restaurantRepository) {
		Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId()).orElse(null);
		return restaurant != null && restaurant.getOwner() != null && restaurant.getOwner().getId() == userId;
	}
}
