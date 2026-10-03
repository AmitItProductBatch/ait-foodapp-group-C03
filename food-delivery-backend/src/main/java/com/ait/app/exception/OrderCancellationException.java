package com.ait.app.exception;

public class OrderCancellationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OrderCancellationException(String mesage) {
		super(mesage);
	}

}
