package com.ait.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

	List<Restaurant> findByActiveTrueAndStatus(String status);

	List<Restaurant> findRestaurantsByCuisine(String cuisine);

}