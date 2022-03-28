package com.devsuperior.dscatalog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.dto.CategoryDto;
import com.devsuperior.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<List<CategoryDto>> findAll() {
		List<CategoryDto> categoriesDto = categoryService.findAll();
		return ResponseEntity.ok().body(categoriesDto);
	}
	
	
	@GetMapping(value = "/{id}")
	//@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<CategoryDto> findById(@PathVariable Long id) {
		CategoryDto categoryDto = categoryService.findById(id);
		return ResponseEntity.ok().body(categoryDto);
	}

}
