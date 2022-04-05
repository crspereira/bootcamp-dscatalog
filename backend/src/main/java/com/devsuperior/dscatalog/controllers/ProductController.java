package com.devsuperior.dscatalog.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
//	@GetMapping
//	public ResponseEntity<Page<ProductDto>> findAll(
//			@RequestParam(value = "page", defaultValue = "0") Integer page,
//			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
//			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
//			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy
//			) {
//		
//		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
//		
//		Page<ProductDto> productsDto = productService.findAllPaged(pageRequest);
//		return ResponseEntity.ok().body(productsDto);
//	}
	
	@GetMapping
	public ResponseEntity<Page<ProductDto>> findAll(@PageableDefault(page = 0, size = 12, sort = "name", direction = Direction.DESC) Pageable pageable) {
		Page<ProductDto> productsDto = productService.findAllPaged(pageable);
		return ResponseEntity.ok().body(productsDto);
	}
	
	@GetMapping(value = "/{id}")
	//@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
		ProductDto productDto = productService.findById(id);
		return ResponseEntity.ok().body(productDto);
	}
	
	@PostMapping
	public ResponseEntity<ProductDto> insert(@RequestBody ProductDto productDto) {
		productDto = productService.insert(productDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
				.buildAndExpand(productDto.getId()).toUri();
		return ResponseEntity.created(uri).body(productDto);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto productDto) {
		productDto = productService.update(id, productDto);
		return ResponseEntity.ok().body(productDto);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
}
