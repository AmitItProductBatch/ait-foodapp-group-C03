package com.ait.app.util;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import com.ait.app.enums.OrderStatus;

public class OrderStatusTransitionValidator {

	private static final EnumMap<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new EnumMap<>(OrderStatus.class);

	static {
		Set<OrderStatus> placedTransitions = new HashSet<>();
		placedTransitions.add(OrderStatus.CONFIRMED);
		placedTransitions.add(OrderStatus.CANCELLED);
		VALID_TRANSITIONS.put(OrderStatus.PLACED, placedTransitions);

		Set<OrderStatus> confirmedTransitions = new HashSet<>();
		confirmedTransitions.add(OrderStatus.PREPARING);
		confirmedTransitions.add(OrderStatus.CANCELLED);
		VALID_TRANSITIONS.put(OrderStatus.CONFIRMED, confirmedTransitions);

		Set<OrderStatus> preparingTransitions = new HashSet<>();
		preparingTransitions.add(OrderStatus.READY);
		preparingTransitions.add(OrderStatus.CANCELLED);
		VALID_TRANSITIONS.put(OrderStatus.PREPARING, preparingTransitions);

		Set<OrderStatus> readyTransitions = new HashSet<>();
		readyTransitions.add(OrderStatus.OUT_FOR_DELIVERY);
		VALID_TRANSITIONS.put(OrderStatus.READY, readyTransitions);

		Set<OrderStatus> outForDeliveryTransitions = new HashSet<>();
		outForDeliveryTransitions.add(OrderStatus.DELIVERED);
		VALID_TRANSITIONS.put(OrderStatus.OUT_FOR_DELIVERY, outForDeliveryTransitions);

		Set<OrderStatus> deliveredTransitions = new HashSet<>();
		VALID_TRANSITIONS.put(OrderStatus.DELIVERED, deliveredTransitions);

		Set<OrderStatus> cancelledTransitions = new HashSet<>();
		VALID_TRANSITIONS.put(OrderStatus.CANCELLED, cancelledTransitions);
	}

	public static boolean isValidTransition(OrderStatus fromStatus, OrderStatus toStatus) {
		if (fromStatus == null || toStatus == null) {
			return false;
		}

		Set<OrderStatus> allowedTransitions = VALID_TRANSITIONS.get(fromStatus);
		return allowedTransitions != null && allowedTransitions.contains(toStatus);
	}

	public static String getErrorMessage(OrderStatus fromStatus, OrderStatus toStatus) {
		return String.format("Invalid status transition from %s to %s", fromStatus, toStatus);
	}
}
