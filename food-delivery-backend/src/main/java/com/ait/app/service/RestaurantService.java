
package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.RestaurantDetailsDTO;
import com.ait.app.dto.RestaurantListResponseDTO;
import com.ait.app.dto.RestaurantRequestDTO;
import com.ait.app.dto.RestaurantResponseDTO;

public interface RestaurantService {

    RestaurantResponseDTO createRestaurant(RestaurantRequestDTO requestDTO);

    RestaurantDetailsDTO getRestaurantDetails(Integer id);

	List<RestaurantListResponseDTO> getAllRestaurants();

	List<RestaurantListResponseDTO> getRestaurantsByCuisine(String cuisine);
}
