package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ait.app.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

	Optional<Feedback> findByUserIdAndOrderId(Integer userId, Integer orderId);

	@Query("SELECT f FROM Feedback f WHERE f.restaurantId = :restaurantId AND f.deleted = false AND f.flagged = false AND f.published = true ORDER BY f.createdAt DESC")
	Page<Feedback> findByRestaurantIdWithFilters(@Param("restaurantId") Integer restaurantId, Pageable pageable);

	@Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.restaurantId = :restaurantId AND f.deleted = false AND f.flagged = false AND f.published = true")
	Double calculateAverageRating(@Param("restaurantId") Integer restaurantId);

	@Query("SELECT COUNT(f) FROM Feedback f WHERE f.restaurantId = :restaurantId AND f.deleted = false AND f.flagged = false AND f.published = true")
	Long countFeedbackByRestaurantId(@Param("restaurantId") Integer restaurantId);
}
