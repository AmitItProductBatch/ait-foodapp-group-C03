package com.ait.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ait.app.entity.Cart;
import com.ait.app.repository.CartRepository;

@SpringBootTest
class CartRepositoryIntegrationTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    void shouldPersistCart() {

        Cart cart = new Cart();

        cart.setUserId(1001);
        cart.setRestaurantId(null);
        cart.setTotalAmount(0.0);

        Cart savedCart = cartRepository.saveAndFlush(cart);

        assertThat(savedCart.getId()).isNotNull();
        assertThat(savedCart.getUserId()).isEqualTo(1001);
        assertThat(savedCart.getRestaurantId()).isNull();
        assertThat(savedCart.getTotalAmount()).isEqualTo(0.0);
        assertThat(savedCart.getCreatedAt()).isNotNull();
        assertThat(savedCart.getUpdatedAt()).isNotNull();

        Optional<Cart> fetchedCart =
                cartRepository.findById(savedCart.getId());

        assertThat(fetchedCart).isPresent();
        assertThat(fetchedCart.get().getUserId()).isEqualTo(1001);
    }
}