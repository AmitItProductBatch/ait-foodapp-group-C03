package com.ait.app.service;

import com.ait.app.dto.RatingRequestDTO;
import com.ait.app.dto.RatingResponseDTO;

public interface RatingService {

	RatingResponseDTO createOrUpdateRating(RatingRequestDTO request);
}
