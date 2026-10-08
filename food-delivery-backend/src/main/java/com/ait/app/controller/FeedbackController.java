package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.FeedbackRequestDTO;
import com.ait.app.dto.FeedbackResponseDTO;
import com.ait.app.dto.FeedbackUpdateRequestDTO;
import com.ait.app.dto.PaginatedFeedbackResponseDTO;
import com.ait.app.dto.RatingRequestDTO;
import com.ait.app.dto.RatingResponseDTO;
import com.ait.app.service.FeedbackService;
import com.ait.app.service.RatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private RatingService ratingService;

	@PostMapping
	public ResponseEntity<FeedbackResponseDTO> createFeedback(@Valid @RequestBody FeedbackRequestDTO request) {
		FeedbackResponseDTO response = feedbackService.createFeedback(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/ratings")
	public ResponseEntity<RatingResponseDTO> createOrUpdateRating(@Valid @RequestBody RatingRequestDTO request) {
		RatingResponseDTO response = ratingService.createOrUpdateRating(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<PaginatedFeedbackResponseDTO> getFeedbackByRestaurantId(@PathVariable Integer restaurantId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PaginatedFeedbackResponseDTO response = feedbackService.getFeedbackByRestaurantId(restaurantId, page, size);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/{feedbackId}")
	public ResponseEntity<FeedbackResponseDTO> updatedFeedback(@PathVariable Integer feedbackId,
			@RequestHeader(value = "X-User-Id", required = false) Integer userId,
			@Valid @RequestBody FeedbackUpdateRequestDTO request) {
		FeedbackResponseDTO response = feedbackService.updateFeedback(feedbackId, userId, request);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{feedbackId}")
	public ResponseEntity<Void> deleteFeedback(@PathVariable Integer feedbackId,
			@RequestHeader(value = "X-User-Id", required = false) Integer userId,
			@RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {
		feedbackService.deleteFeedback(feedbackId, userId, userRole);
		return ResponseEntity.noContent().build();
	}
}
