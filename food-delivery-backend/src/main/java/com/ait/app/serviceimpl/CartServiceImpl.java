package com.ait.app.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import com.ait.app.dto.CartRequestDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.repository.CartItemRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

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

    @Override
    @Transactional
    public void deleteCartItem(Integer itemId, Integer userId) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findByIdAndCartUserId(itemId, userId);

        if (cartItemOptional.isEmpty()) {
            throw new EntityNotFoundException("Cart item not found or does not belong to user");
        }

        CartItem cartItem = cartItemOptional.get();
        Cart cart = cartItem.getCart();

        cartItemRepository.delete(cartItem);

        cartRepository.flush();

        Cart refreshedCart = cartRepository.findById(cart.getId()).orElseThrow();

        double newTotal = 0.0;
        for (CartItem item : refreshedCart.getCartItems()) {
            newTotal += item.getPrice() * item.getQuantity();
        }

        refreshedCart.setTotalAmount(newTotal);

        if (refreshedCart.getCartItems().isEmpty()) {
            refreshedCart.setRestaurantId(null);
        }

        cartRepository.save(refreshedCart);
    }
}