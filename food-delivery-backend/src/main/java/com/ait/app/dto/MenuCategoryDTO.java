package com.ait.app.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuCategoryDTO {

	private String category;
	private List<MenuItemDetailDTO> items = new ArrayList<>();

	public MenuCategoryDTO() {
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<MenuItemDetailDTO> getItems() {
		return items;
	}

	public void setItems(List<MenuItemDetailDTO> items) {
		this.items = items;
	}
}
