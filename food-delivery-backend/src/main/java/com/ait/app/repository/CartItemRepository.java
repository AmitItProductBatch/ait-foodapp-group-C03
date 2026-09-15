package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

	
    Optional<CartItem> findByCartIdAndMenuItemId(Integer cartId,Long menuItemId);
}