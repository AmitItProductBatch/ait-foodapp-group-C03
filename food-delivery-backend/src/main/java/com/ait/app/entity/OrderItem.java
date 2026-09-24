package com.ait.app.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "menu_item_id", nullable = false)
	private Integer menuItemId;

	@Column(name = "item_name_snapshot", nullable = false, length = 200)
	private String itemNameSnapshot;

	@Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
	@Check(name = "check_unit_price_non_negative", constraints = "unit_price >= 0")
	private BigDecimal unitPrice;

	@Column(name = "quantity", nullable = false)
	@Check(name = "check_quantity_positive", constraints = "quantity >= 1")
	private Integer quantity;

	@Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@JsonIgnore
	private Order order;

	public OrderItem() {
	}

	public OrderItem(Integer menuItemId, String itemNameSnapshot, BigDecimal unitPrice, Integer quantity) {
		this.menuItemId = menuItemId;
		this.itemNameSnapshot = itemNameSnapshot;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.calculateSubtotal();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Integer menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getItemNameSnapshot() {
		return itemNameSnapshot;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
		this.calculateSubtotal();
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	private void calculateSubtotal() {
		if (unitPrice != null && quantity != null) {
			this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
		} else {
			this.subtotal = BigDecimal.ZERO;
		}
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}