package com.ait.app.service;

import com.ait.app.entity.Order;
import com.ait.app.enums.OrderStatus;

public interface NotificationService {

	void sendOrderStatusUpdateNotification(Order order, OrderStatus oldStatus, OrderStatus newStatus);
}
