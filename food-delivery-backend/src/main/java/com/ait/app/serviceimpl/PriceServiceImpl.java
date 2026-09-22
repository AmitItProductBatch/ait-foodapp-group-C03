package com.ait.app.serviceimpl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.OrderTotalResponseDTO;
import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.MenuItem;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.service.PriceService;

@Service
public class PriceServiceImpl implements PriceService {

	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private CartRepository cartRepository;

	@Override
	public PriceCalculationResponseDTO calculatePrice(PriceCalculationRequestDTO request) {

		MenuItem menuItem = menuItemRepository.findById(request.getItemId())
				.orElseThrow(() -> new RuntimeException("Menu item not found"));

		if (menuItem.getAvailability() == null || !menuItem.getAvailability()) {

			throw new RuntimeException("Menu item is not available");
		}

		BigDecimal unitPrice = BigDecimal.valueOf(menuItem.getPrice());

		BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

		BigDecimal discount = BigDecimal.ZERO;

		BigDecimal finalSubtotal = subtotal.subtract(discount);

		return new PriceCalculationResponseDTO(menuItem.getId(), request.getQuantity(), unitPrice, discount,
				finalSubtotal);
	}

	@Override
	public OrderTotalResponseDTO calculateOrderTotal(Integer userId) {
		Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
		if (optionalCart.isEmpty()) {
			throw new ResourceNotFoundException("Cart", userId);
		}
		Cart cart = optionalCart.get();

		if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
			throw new InvalidRequestException("Cart is empty");
		}
		BigDecimal total = BigDecimal.ZERO;
		for (CartItem item : cart.getCartItems()) {
			BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			total = total.add(itemTotal);
		}

		return new OrderTotalResponseDTO(total);
	}
}