package com.ait.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

	boolean existsByRestaurantIdAndNameIgnoreCase(int restaurantId, String name);

	boolean existsByRestaurantIdAndNameIgnoreCaseAndDeletedFalse(int restaurantId, String name);
	
	List<MenuItem> findByRestaurantIdAndDeletedFalse(int restaurantId);

}
