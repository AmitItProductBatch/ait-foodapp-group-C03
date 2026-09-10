package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.BadRequestException;
import com.ait.app.exception.ResourceNotFoundException;
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
    public MenuItemResponseDTO createMenuItem(
            int restaurantId,
            MenuItemRequestDTO requestDTO) {

        // 1. Validate restaurant
        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurant not found with ID: "
                                + restaurantId));

        // 2. Validate admin
        User admin = userRepository
                .findById(requestDTO.getAdminId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin not found with ID: "
                                + requestDTO.getAdminId()));

        // 3. Verify admin role
        if (admin.getRole() == null ||
                !"PARTNER".equalsIgnoreCase(admin.getRole())) {

            throw new BadRequestException(
                    "User is not a restaurant administrator");
        }

        // 4. Verify admin owns the restaurant
        if (restaurant.getOwner() == null ||
                restaurant.getOwner().getId() != admin.getId()) {

            throw new BadRequestException(
                    "You are not authorized to add menu items "
                    + "to this restaurant");
        }

        // 5. Verify restaurant is active
        if (!Boolean.TRUE.equals(restaurant.getActive())) {

            throw new BadRequestException(
                    "Restaurant is not active");
        }

        // 6. Validate price
        if (requestDTO.getPrice() == null ||
                requestDTO.getPrice() <= 0) {

            throw new BadRequestException(
                    "Price must be greater than 0");
        }

        // 7. Check duplicate menu item name
        boolean exists =
                menuItemRepository
                    .existsByRestaurantIdAndNameIgnoreCase(
                            restaurantId,
                            requestDTO.getName()
                    );

        if (exists) {

            throw new BadRequestException(
                    "Menu item with name '"
                    + requestDTO.getName()
                    + "' already exists in this restaurant");
        }

        // 8. Create MenuItem
        MenuItem menuItem = new MenuItem();

        menuItem.setName(requestDTO.getName());
        menuItem.setDescription(requestDTO.getDescription());
        menuItem.setPrice(requestDTO.getPrice());
        menuItem.setAvailability(
                requestDTO.getAvailability()
        );
        menuItem.setCategory(requestDTO.getCategory());

        // Set relationship
        menuItem.setRestaurant(restaurant);

        // 9. Save
        MenuItem savedItem =
                menuItemRepository.save(menuItem);

        // 10. Return response DTO
        return new MenuItemResponseDTO(
                savedItem.getId(),
                restaurant.getId(),
                savedItem.getName(),
                savedItem.getDescription(),
                savedItem.getPrice(),
                savedItem.getAvailability(),
                savedItem.getCategory(),
                "Menu item created successfully"
        );
    }
}