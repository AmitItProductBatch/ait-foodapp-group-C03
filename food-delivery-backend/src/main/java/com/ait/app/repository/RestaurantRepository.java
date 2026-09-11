package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

}