package com.ait.app.serviceimpl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;
import com.ait.app.entity.MenuItem;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.service.PriceService;

@Service
public class PriceServiceImpl implements PriceService {

	@Autowired
	private MenuItemRepository menuItemRepository;

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
}