package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ait.app.entity.Order;
import com.ait.app.entity.User;
import com.ait.app.enums.OrderStatus;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void sendOrderStatusUpdateNotification(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
		try {
			User user = userRepository.findById(order.getUserId()).orElse(null);
			if (user == null || user.getEmail() == null) {
				return;
			}

			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setSubject("Order Status Update - Order #" + order.getId());
			message.setText(buildEmailBody(order, oldStatus, newStatus, user.getName()));

			mailSender.send(message);
		} catch (Exception e) {
		}
	}

	private String buildEmailBody(Order order, OrderStatus oldStatus, OrderStatus newStatus, String userName) {
		StringBuilder body = new StringBuilder();
		body.append("Dear ").append(userName).append(",\n\n");
		body.append("Your order #").append(order.getId()).append(" status has been updated.\n\n");
		body.append("Previous Status: ").append(oldStatus).append("\n");
		body.append("New Status: ").append(newStatus).append("\n\n");
		body.append("Order Details:\n");
		body.append("- Total Amount: $").append(order.getTotalAmount()).append("\n");
		body.append("- Delivery Address: ").append(order.getDeliveryAddressSnapshot()).append("\n\n");
		body.append("Thank you for your order!\n");
		body.append("Food Delivery Team");
		return body.toString();
	}
}
