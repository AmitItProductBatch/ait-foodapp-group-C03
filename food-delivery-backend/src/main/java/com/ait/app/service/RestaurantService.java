
package com.ait.app.service;

import com.ait.app.dto.RestaurantDetailsDTO;
import com.ait.app.dto.RestaurantRequestDTO;
import com.ait.app.dto.RestaurantResponseDTO;

public interface RestaurantService {

    RestaurantResponseDTO createRestaurant(RestaurantRequestDTO requestDTO);

    RestaurantDetailsDTO getRestaurantDetails(Integer id);
}