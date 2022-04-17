package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.dto.CategoryDto;
import com.devsuperior.dscatalog.entities.Category;

public class CategoryFactory {
	
	public static Category createCategory() {
		Category category = new Category(2L, "Electronics");
		return category;
	}
	
	public static CategoryDto createCategoryDto() {
		Category category = createCategory();
		return new CategoryDto(category);
	}

}
