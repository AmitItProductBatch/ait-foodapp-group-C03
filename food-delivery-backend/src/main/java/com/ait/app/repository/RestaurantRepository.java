
package com.ait.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Optional<Restaurant> findByIdAndActiveTrue(Integer id);

	List<Restaurant> findByActiveTrueAndStatus(String status);

	List<Restaurant> findRestaurantsByCuisine(String cuisine);

}
