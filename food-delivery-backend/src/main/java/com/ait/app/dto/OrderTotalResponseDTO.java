package com.ait.app.dto;

import java.math.BigDecimal;

public class OrderTotalResponseDTO {
	private BigDecimal total;

	public OrderTotalResponseDTO() {

	}

	public OrderTotalResponseDTO(BigDecimal total) {
		this.total = total;

	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}
