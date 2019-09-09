package com.mytestbuddy.citytourguide;

public class CommonCategoryData {

	private String CategoryId;
	private String Category;

	public CommonCategoryData(String CategoryId, String Category) {
		this.CategoryId = CategoryId;
		this.Category = Category;
	}

	// Set & Get CategoryId...
	public void setCategoryId(String CategoryId) {
		this.CategoryId = CategoryId;
	}

	public String getCategoryId() {
		return CategoryId;

	}

	// Set & Get Category...
	public void setCategory(String Category) {
		this.Category = Category;
	}

	public String getCategory() {
		return Category;

	}
}