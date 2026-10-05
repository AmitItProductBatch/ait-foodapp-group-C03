package com.ait.app.dto;

import java.util.List;

public class PaginatedFeedbackResponseDTO {

	private List<FeedbackResponseDTO> feedbacks;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;

	public PaginatedFeedbackResponseDTO() {
	}

	public PaginatedFeedbackResponseDTO(List<FeedbackResponseDTO> feedbacks, int currentPage, int pageSize,
			long totalElements, int totalPages) {
		this.feedbacks = feedbacks;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public List<FeedbackResponseDTO> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<FeedbackResponseDTO> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
}
