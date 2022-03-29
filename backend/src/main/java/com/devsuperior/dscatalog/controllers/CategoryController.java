package com.devsuperior.dscatalog.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.CategoryDto;
import com.devsuperior.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
//	@GetMapping
//	public ResponseEntity<Page<CategoryDto>> findAll(@PageableDefault(page = 0, size = 12, direction = Direction.ASC, sort = "name") Pageable pageable) {
//		Page<CategoryDto> categoriesDto = categoryService.findAllPaged(pageable);
//		return ResponseEntity.ok().body(categoriesDto);
//	}
	
	@GetMapping
	public ResponseEntity<Page<CategoryDto>> findAll(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy
			) {
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		Page<CategoryDto> categoriesDto = categoryService.findAllPaged(pageRequest);
		return ResponseEntity.ok().body(categoriesDto);
	}
	
	@GetMapping(value = "/{id}")
	//@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<CategoryDto> findById(@PathVariable Long id) {
		CategoryDto categoryDto = categoryService.findById(id);
		return ResponseEntity.ok().body(categoryDto);
	}
	
	@PostMapping
	public ResponseEntity<CategoryDto> insert(@RequestBody CategoryDto categoryDto) {
		categoryDto = categoryService.insert(categoryDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
				.buildAndExpand(categoryDto.getId()).toUri();
		return ResponseEntity.created(uri).body(categoryDto);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
		categoryDto = categoryService.update(id, categoryDto);
		return ResponseEntity.ok().body(categoryDto);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		categoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
}
