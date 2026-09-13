package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

	boolean existsByRestaurantIdAndNameIgnoreCase(int restaurantId, String name);

}
