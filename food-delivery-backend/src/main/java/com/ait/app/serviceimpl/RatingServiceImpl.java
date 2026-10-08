package com.ait.app.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.RatingRequestDTO;
import com.ait.app.dto.RatingResponseDTO;
import com.ait.app.entity.Feedback;
import com.ait.app.entity.Order;
import com.ait.app.entity.Restaurant;
import com.ait.app.enums.OrderStatus;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
import com.ait.app.repository.FeedbackRepository;
import com.ait.app.repository.OrderRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Override
	@Transactional
	public RatingResponseDTO createOrUpdateRating(RatingRequestDTO request) {
		Order order = orderRepository.findById(request.getOrderId())
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

		if (!order.getUserId().equals(request.getUserId())) {
			throw new UnauthorizedActionException("User can only rate their own orders");
		}

		if (order.getStatus() != OrderStatus.DELIVERED) {
			throw new InvalidRequestException("Rating is only allowed for delivered orders");
		}

		Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));

		if (restaurant.getId() != order.getRestaurantId()) {
			throw new InvalidRequestException("Restaurant ID does not match the order's restaurant");
		}

		Optional<Feedback> existingFeedback = feedbackRepository.findByUserIdAndOrderId(request.getUserId(),
				request.getOrderId());

		Feedback feedback;
		if (existingFeedback.isPresent()) {
			feedback = existingFeedback.get();
			feedback.setRating(request.getRating());
		} else {
			feedback = new Feedback();
			feedback.setUserId(request.getUserId());
			feedback.setRestaurantId(request.getRestaurantId());
			feedback.setOrderId(request.getOrderId());
			feedback.setRating(request.getRating());
			feedback.setDeleted(false);
			feedback.setFlagged(false);
			feedback.setPublished(true);
		}

		Feedback savedFeedback = feedbackRepository.save(feedback);
		updateRestaurantRating(request.getRestaurantId());

		return convertToRatingResponseDTO(savedFeedback);
	}

	private void updateRestaurantRating(Integer restaurantId) {
		Double avgRating = feedbackRepository.calculateAverageRating(restaurantId);
		Long count = feedbackRepository.countFeedbackByRestaurantId(restaurantId);

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

		if (count > 0 && avgRating != null) {
			restaurant.setRating(avgRating);
		} else {
			restaurant.setRating(null);
		}

		restaurantRepository.save(restaurant);
	}

	private RatingResponseDTO convertToRatingResponseDTO(Feedback feedback) {
		RatingResponseDTO dto = new RatingResponseDTO();
		dto.setId(feedback.getId());
		dto.setUserId(feedback.getUserId());
		dto.setRestaurantId(feedback.getRestaurantId());
		dto.setOrderId(feedback.getOrderId());
		dto.setRating(feedback.getRating());
		dto.setCreatedAt(feedback.getCreatedAt());
		dto.setUpdatedAt(feedback.getUpdatedAt());
		return dto;
	}
}
