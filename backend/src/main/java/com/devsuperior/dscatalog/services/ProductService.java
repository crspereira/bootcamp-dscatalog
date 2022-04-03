package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDto;
import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ServiceDataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ServiceNotFoundException;

@Service
public class ProductService {
	
  /*//Injeção de Dependencia Manual via Construtor
	private ProductRepository productRepository;
	
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}*/
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
//	@Transactional(readOnly = true)
//	public Page<ProductDto> findAllPaged(Pageable pageable) {
//		Page<Product> productsDto = productRepository.findAll(pageable);
//		return productsDto.map(x -> new ProductDto(x));
//	}
	
	@Transactional(readOnly = true)
	public Page<ProductDto> findAllPaged(PageRequest pageRequest) {
		Page<Product> productsDto = productRepository.findAll(pageRequest);
		return productsDto.map(x -> new ProductDto(x));
	}
	
	@Transactional(readOnly = true)
	public ProductDto findById(Long id) {
		Optional<Product> obj = productRepository.findById(id);
		Product product = obj.orElseThrow(() -> new ServiceNotFoundException("Entity Not Found!"));
		return new ProductDto(product, product.getCategories());
	}
	
	@Transactional
	public ProductDto insert(ProductDto productDto) {
		Product product = new Product();
		productDtoToProduct(productDto, product);
		product = productRepository.save(product);
		return new ProductDto(product);	
	}

	@Transactional
	public ProductDto update(Long id, ProductDto productDto) {
		try {
			Product newProduct = productRepository.getById(id); //cria apenas uma referencia da Entidade
			productDtoToProduct(productDto, newProduct);
			newProduct = productRepository.save(newProduct);	
			return new ProductDto(newProduct);
		} catch (EntityNotFoundException e) {
			throw new ServiceNotFoundException("ID ["+ id +"] Not Found!");
		}
	}

	public void delete(Long id) {
		try {
			productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ServiceNotFoundException("ID ["+ id +"] Not Found!");
		} catch (DataIntegrityViolationException e) {
			throw new ServiceDataBaseException("DataBase Integrity Violation!");
		}
	}

	private void productDtoToProduct(ProductDto productDto, Product product) {
		product.setName(productDto.getName());
		product.setDescription(productDto.getDescription());
		product.setPrice(productDto.getPrice());
		product.setImgUrl(productDto.getImgUrl());
		product.setDate(productDto.getDate());
		
		product.getCategories().clear();
		for (CategoryDto catDto : productDto.getCategories()) {
			Category category =  categoryRepository.getById(catDto.getId());
			product.getCategories().add(category);
		}
		
	}
	
}
