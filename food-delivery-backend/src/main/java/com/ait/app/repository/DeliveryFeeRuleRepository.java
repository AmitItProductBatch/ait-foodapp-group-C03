package com.ait.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ait.app.entity.DeliveryFeeRule;

public interface DeliveryFeeRuleRepository extends JpaRepository<DeliveryFeeRule, Integer> {

	@Query("SELECT d FROM DeliveryFeeRule d WHERE d.active = true ORDER BY d.updatedAt DESC")
	List<DeliveryFeeRule> findAllActiveOrderByUpdatedAtDesc();
}
