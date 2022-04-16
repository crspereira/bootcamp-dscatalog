package com.devsuperior.dscatalog.factory;

import java.time.Instant;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class ProductFactory {
	
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2022-04-16T04:00:00Z"));
		product.getCategories().add(new Category(2L, "Electronics"));
		return product;
	}
	
	public static ProductDto createProductDto() {
		Product product = createProduct();
		return new ProductDto(product, product.getCategories());
	}

}
