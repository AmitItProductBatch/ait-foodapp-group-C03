package com.ait.app.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.FeedbackRequestDTO;
import com.ait.app.dto.FeedbackResponseDTO;
import com.ait.app.dto.PaginatedFeedbackResponseDTO;
import com.ait.app.entity.Feedback;
import com.ait.app.entity.Order;
import com.ait.app.entity.Restaurant;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
import com.ait.app.repository.FeedbackRepository;
import com.ait.app.repository.OrderRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Override
	@Transactional
	public FeedbackResponseDTO createFeedback(FeedbackRequestDTO request) {
		Optional<Feedback> existingFeedback = feedbackRepository.findByUserIdAndOrderId(request.getUserId(),
				request.getOrderId());

		if (existingFeedback.isPresent()) {
			throw new InvalidRequestException("Feedback already exists for this order");
		}

		Order order = orderRepository.findById(request.getOrderId())
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

		if (!order.getUserId().equals(request.getUserId())) {
			throw new UnauthorizedActionException("User can only provide feedback for their own orders");
		}

		Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));

		if (restaurant.getId() != order.getRestaurantId()) {
			throw new InvalidRequestException("Restaurant ID does not match the order's restaurant");
		}

		Feedback feedback = new Feedback();
		feedback.setUserId(request.getUserId());
		feedback.setRestaurantId(request.getRestaurantId());
		feedback.setOrderId(request.getOrderId());
		feedback.setRating(request.getRating());
		feedback.setComment(request.getComment());
		feedback.setDeleted(false);
		feedback.setFlagged(false);
		feedback.setPublished(true);

		Feedback savedFeedback = feedbackRepository.save(feedback);

		updateRestaurantRating(request.getRestaurantId());

		return convertToResponseDTO(savedFeedback);
	}

	@Override
	@Transactional
	public void deleteFeedback(Integer feedbackId, Integer userId, String userRole) {
		Feedback feedback = feedbackRepository.findById(feedbackId)
				.orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

		if (!isAdmin(userRole) && !feedback.getUserId().equals(userId)) {
			throw new UnauthorizedActionException("User is not authorized to delete this feedback");
		}

		feedback.setDeleted(true);
		feedbackRepository.save(feedback);

		updateRestaurantRating(feedback.getRestaurantId());
	}

	@Override
	public PaginatedFeedbackResponseDTO getFeedbackByRestaurantId(Integer restaurantId, int page, int size) {
		if (page < 0) {
			page = 0;
		}

		if (size <= 0) {
			size = 10;
		}

		if (size > 100) {
			size = 100;
		}

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

		Pageable pageable = PageRequest.of(page, size);
		Page<Feedback> feedbackPage = feedbackRepository.findByRestaurantIdWithFilters(restaurantId, pageable);

		List<FeedbackResponseDTO> feedbackDTOs = feedbackPage.getContent().stream()
				.map(this::convertToResponseDTO)
				.collect(Collectors.toList());

		return new PaginatedFeedbackResponseDTO(feedbackDTOs, feedbackPage.getNumber(), feedbackPage.getSize(),
				feedbackPage.getTotalElements(), feedbackPage.getTotalPages());
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

	private boolean isAdmin(String userRole) {
		return "ADMIN".equalsIgnoreCase(userRole);
	}

	private FeedbackResponseDTO convertToResponseDTO(Feedback feedback) {
		FeedbackResponseDTO dto = new FeedbackResponseDTO();
		dto.setId(feedback.getId());
		dto.setUserId(feedback.getUserId());
		dto.setRestaurantId(feedback.getRestaurantId());
		dto.setOrderId(feedback.getOrderId());
		dto.setRating(feedback.getRating());
		dto.setComment(feedback.getComment());
		dto.setCreatedAt(feedback.getCreatedAt());
		dto.setUpdatedAt(feedback.getUpdatedAt());
		return dto;
	}
}
