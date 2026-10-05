
package com.ait.app.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Order;
import com.ait.app.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	Page<Order> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

	Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Integer userId, OrderStatus status, Pageable pageable);

	Page<Order> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Integer userId, LocalDateTime fromDate,
			LocalDateTime toDate, Pageable pageable);

	Page<Order> findByUserIdAndStatusAndCreatedAtBetweenOrderByCreatedAtDesc(Integer userId, OrderStatus status,
			LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}