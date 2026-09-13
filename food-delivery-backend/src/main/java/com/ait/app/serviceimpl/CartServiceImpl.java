package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CartRequestDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.entity.Cart;
import com.ait.app.repository.CartRepository;
import com.ait.app.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public CartResponseDTO createCart(CartRequestDTO request) {

        if (cartRepository.existsByUserId(request.getUserId())) {

            throw new RuntimeException(
                "Cart already exists for user ID: "
                + request.getUserId()
            );
        }

        Cart cart = new Cart();

        cart.setUserId(request.getUserId());
        cart.setRestaurantId(request.getRestaurantId());

        cart.setTotalAmount(0.0);

        Cart savedCart = cartRepository.save(cart);

        CartResponseDTO response = new CartResponseDTO();

        response.setId(savedCart.getId());
        response.setUserId(savedCart.getUserId());
        response.setRestaurantId(savedCart.getRestaurantId());
        response.setTotalAmount(savedCart.getTotalAmount());
        response.setCreatedAt(savedCart.getCreatedAt());
        response.setUpdatedAt(savedCart.getUpdatedAt());

        return response;
    }
}