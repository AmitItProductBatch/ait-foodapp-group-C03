package com.ait.app.service;

import com.ait.app.dto.FeedbackRequestDTO;
import com.ait.app.dto.FeedbackResponseDTO;
import com.ait.app.dto.FeedbackUpdateRequestDTO;
import com.ait.app.dto.PaginatedFeedbackResponseDTO;

public interface FeedbackService {

	FeedbackResponseDTO createFeedback(FeedbackRequestDTO request);

	void deleteFeedback(Integer feedbackId, Integer userId, String userRole);

	PaginatedFeedbackResponseDTO getFeedbackByRestaurantId(Integer restaurantId, int page, int size);

	FeedbackResponseDTO updateFeedback(Integer feedbackId, Integer userId, FeedbackUpdateRequestDTO request);
}
