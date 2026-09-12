
package com.ait.app.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantDetailsDTO;
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
    public RestaurantResponseDTO createRestaurant(
            RestaurantRequestDTO requestDTO) {

        if (requestDTO.getOwnerId() == null) {
            throw new RuntimeException("Owner ID is required");
        }

        int ownerId = requestDTO.getOwnerId();

        Optional<User> optionalUser =
                userRepository.findById(ownerId);

        User owner;

        if (optionalUser.isPresent()) {
            owner = optionalUser.get();
        } else {
            throw new RuntimeException("Owner not found");
        }

        if (owner.getRole() == null
                || !"PARTNER".equalsIgnoreCase(owner.getRole())) {

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

        restaurant.setHours(requestDTO.getHours());

        restaurant.setOwner(owner);

        restaurant.setStatus("PENDING");
        restaurant.setActive(false);

        Restaurant savedRestaurant =
                restaurantRepository.save(restaurant);

        return new RestaurantResponseDTO(
                savedRestaurant.getId(),
                owner.getId(),
                "Restaurant created successfully",
                savedRestaurant.getStatus()
        );
    }

    @Override
    public RestaurantDetailsDTO getRestaurantDetails(Integer id) {

        Optional<Restaurant> optionalRestaurant =
                restaurantRepository.findByIdAndActiveTrue(id);

        if (optionalRestaurant.isEmpty()) {
            throw new RuntimeException(
                    "Restaurant not found or inactive");
        }

        Restaurant restaurant = optionalRestaurant.get();

        RestaurantDetailsDTO dto = new RestaurantDetailsDTO();

        dto.setRestaurantId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setHours(restaurant.getHours());
        dto.setCuisine(restaurant.getCuisine());

        // Rating tumhi feedback table madun ghya foreign key use kara ithya maunally takta yatye using postmanetc..
        dto.setRating(0.0);

        return dto;
    }
}

