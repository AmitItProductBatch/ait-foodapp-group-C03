/*ackage com.ait.app.serviceimpl;

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
}*//*
package com.ait.app.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ait.app.dto.DeliveryFeeRequestDTO;
import com.ait.app.dto.DeliveryFeeResponseDTO;
import com.ait.app.dto.OrderTotalResponseDTO;
import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;
import com.ait.app.entity.Address;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.DistanceCalculator;
import com.ait.app.entity.GeoLocation;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.AddressRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.GeocodingService;
import com.ait.app.service.PriceService;


@Service
public class PriceServiceImpl implements PriceService {

	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private GeocodingService geocodingService;

	@Value("${delivery.fee.base:2.50}")
	private BigDecimal baseFee;

	@Value("${delivery.fee.per-km:0.75}")
	private BigDecimal perKmRate;

	@Value("${delivery.fee.free-threshold-km:3.0}")
	private double freeDeliveryThresholdKm;

	@Value("${delivery.max-radius-km:15.0}")
	private double maxRadiusKm;

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

	@Override
	public DeliveryFeeResponseDTO calculateDeliveryFee(DeliveryFeeRequestDTO request) {

		Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant", request.getRestaurantId()));

		Address address = addressRepository.findById(request.getAddressId())
				.orElseThrow(() -> new ResourceNotFoundException("Address", request.getAddressId()));

		GeoLocation restaurantLocation = geocodingService.resolve(restaurant.getAddress());
		GeoLocation deliveryLocation = geocodingService.resolve(buildAddressString(address));

		double distanceKm = DistanceCalculator
				.round2(DistanceCalculator.haversineKm(restaurantLocation, deliveryLocation));

		if (distanceKm > maxRadiusKm) {
			throw new InvalidRequestException(
					"Delivery address is outside the maximum delivery radius of " + maxRadiusKm + " km");
		}

		BigDecimal distanceFee = perKmRate.multiply(BigDecimal.valueOf(distanceKm)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal roundedBaseFee = baseFee.setScale(2, RoundingMode.HALF_UP);

		boolean freeDeliveryApplied = distanceKm <= freeDeliveryThresholdKm;

		BigDecimal deliveryFee = freeDeliveryApplied
				? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
				: roundedBaseFee.add(distanceFee).setScale(2, RoundingMode.HALF_UP);

		return new DeliveryFeeResponseDTO(restaurant.getId(), address.getId(), distanceKm, roundedBaseFee,
				distanceFee, deliveryFee, freeDeliveryApplied);
	}

	private String buildAddressString(Address address) {
		return Stream.of(address.getStreetAddress(), address.getApartment(), address.getLandmark(),
				address.getCity(), address.getPostalCode())
				.filter(part -> part != null && !part.isBlank())
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
	}
}
*/
package com.ait.app.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ait.app.dto.DeliveryFeeRequestDTO;
import com.ait.app.dto.DeliveryFeeResponseDTO;
import com.ait.app.dto.OrderTotalResponseDTO;
import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;
import com.ait.app.entity.Address;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.DistanceCalculator;
import com.ait.app.entity.GeoLocation;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.AddressRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.GeocodingService;
import com.ait.app.service.PriceService;


@Service
public class PriceServiceImpl implements PriceService {

	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private GeocodingService geocodingService;

	@Value("${delivery.fee.base:2.50}")
	private BigDecimal baseFee;

	@Value("${delivery.fee.per-km:0.75}")
	private BigDecimal perKmRate;

	@Value("${delivery.fee.free-threshold-km:3.0}")
	private double freeDeliveryThresholdKm;

	@Value("${delivery.max-radius-km:15.0}")
	private double maxRadiusKm;

	// Optional: appended to every geocoding candidate if the address doesn't
	// already look like it includes a country, since Nominatim resolves much
	// more reliably with a country hint. Leave blank to disable.
	@Value("${geocoding.default-country:}")
	private String defaultCountry;

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

	@Override
	public DeliveryFeeResponseDTO calculateDeliveryFee(DeliveryFeeRequestDTO request) {

		Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant", request.getRestaurantId()));

		Address address = addressRepository.findById(request.getAddressId())
				.orElseThrow(() -> new ResourceNotFoundException("Address", request.getAddressId()));

		GeoLocation restaurantLocation = geocodingService
				.resolveBestEffort(buildRestaurantAddressCandidates(restaurant));
		GeoLocation deliveryLocation = geocodingService
				.resolveBestEffort(buildDeliveryAddressCandidates(address));

		double distanceKm = DistanceCalculator
				.round2(DistanceCalculator.haversineKm(restaurantLocation, deliveryLocation));

		if (distanceKm > maxRadiusKm) {
			throw new InvalidRequestException("Delivery address is " + distanceKm
					+ " km away, which exceeds the maximum delivery radius of " + maxRadiusKm + " km");
		}

		BigDecimal distanceFee = perKmRate.multiply(BigDecimal.valueOf(distanceKm)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal roundedBaseFee = baseFee.setScale(2, RoundingMode.HALF_UP);

		boolean freeDeliveryApplied = distanceKm <= freeDeliveryThresholdKm;

		BigDecimal deliveryFee = freeDeliveryApplied
				? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
				: roundedBaseFee.add(distanceFee).setScale(2, RoundingMode.HALF_UP);

		return new DeliveryFeeResponseDTO(restaurant.getId(), address.getId(), distanceKm, roundedBaseFee,
				distanceFee, deliveryFee, freeDeliveryApplied);
	}

	/**
	 * Restaurant addresses are stored as a single free-text field, so the only
	 * fallback available is (optionally) appending a country hint.
	 */
	private List<String> buildRestaurantAddressCandidates(Restaurant restaurant) {
		List<String> candidates = new ArrayList<>();
		String raw = restaurant.getAddress();
		if (raw != null && !raw.isBlank()) {
			candidates.add(withCountryHint(raw.trim()));
			candidates.add(raw.trim());
		}
		return candidates;
	}

	/**
	 * Builds an ordered list of geocoding queries for a delivery address, from
	 * most specific to most general. Apartment/society names and landmarks are
	 * rarely indexed by free geocoders, so if the fully detailed address fails
	 * to resolve we progressively drop detail rather than failing the whole
	 * request - an approximate (city/postal-code-level) distance is far more
	 * useful to the customer than a hard 400.
	 */
	private List<String> buildDeliveryAddressCandidates(Address address) {
		List<String> candidates = new ArrayList<>();

		// 1. Everything we have (street, apartment, landmark, city, postal code).
		candidates.add(joinNonBlank(address.getStreetAddress(), address.getApartment(), address.getLandmark(),
				address.getCity(), address.getPostalCode()));

		// 2. Drop apartment/landmark - building/society names are rarely mapped.
		candidates.add(joinNonBlank(address.getStreetAddress(), address.getCity(), address.getPostalCode()));

		// 3. Postal code + city only - usually resolves to a locality centroid.
		candidates.add(joinNonBlank(address.getPostalCode(), address.getCity()));

		// 4. City only, as an absolute last resort.
		candidates.add(address.getCity());

		List<String> withCountry = candidates.stream()
				.filter(c -> c != null && !c.isBlank())
				.map(this::withCountryHint)
				.collect(Collectors.toList());

		// Also try each candidate without the country hint, in case that's what
		// trips up the geocoder for a given address.
		withCountry.addAll(candidates.stream().filter(c -> c != null && !c.isBlank()).collect(Collectors.toList()));

		return withCountry.stream().distinct().collect(Collectors.toList());
	}

	private String withCountryHint(String query) {
		if (defaultCountry == null || defaultCountry.isBlank()) {
			return query;
		}
		if (query.toLowerCase().contains(defaultCountry.toLowerCase())) {
			return query;
		}
		return query + ", " + defaultCountry;
	}

	private String joinNonBlank(String... parts) {
		return Stream.of(parts)
				.filter(part -> part != null && !part.isBlank())
				.collect(Collectors.joining(", "));
	}
}
