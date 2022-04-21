package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.CategoryFactory;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ServiceDataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ServiceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	// variáveis auxiliares
	private long existingId;	
	private long nonExistingId;
	private long dependentId;
	private long existingCategoryId;
	private Product product;
	private Category category;
	private ProductDto productDto;
	private PageImpl<Product> page; //instância fake de uma página
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingId = 1L;
		nonExistingId = 30L;
		dependentId= 3L;
		product = ProductFactory.createProduct();
		category = CategoryFactory.createCategory();
		existingCategoryId = category.getId();
		productDto = ProductFactory.createProductDto();
		page = new PageImpl<>(List.of(product)); //uma página fake com a lista de um produto nela
		
		// configura ação simulada do comportamento "findAll" do Mock productRepository
		Mockito.when(productRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
		
		// configura ação simulada do comportamento "getById" do Mock productRepository
		Mockito.when(productRepository.getById(existingId)).thenReturn(product);
		Mockito.when(productRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRepository.getById(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		// configura ação simulada do comportamento "findById" do Mock productRepository
		Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		// configura ação simulada do comportamento "save" do Mock productRepository
		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
		
		// configura ação simulada do comportamento "deleteById" do Mock productRepository
		Mockito.doNothing().when(productRepository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
	}

	@Test
	public void findAllPageShouldReturnPage() {
		// arrange
		Pageable page = PageRequest.of(0, 12);
		
		// action
		Page<ProductDto> result = service.findAllPaged(page);
		
		// assert
		Assertions.assertNotNull(result);
		Mockito.verify(productRepository, Mockito.times(1)).findAll(page);
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() {
		// arrange
		//long existingId = 1L;
		
		// action
		ProductDto result = service.findById(existingId);
		
		// assert
		Assertions.assertNotNull(result);
		Mockito.verify(productRepository, Mockito.times(1)).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowsServiceNotFoundExceptionWhenIdDoesNotExist() {
		// arrange
		//long existingId = 1L;
		
		// action
		Assertions.assertThrows(ServiceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		
		// assert
		Mockito.verify(productRepository, Mockito.times(1)).findById(nonExistingId);
	}
	
	@Test
	public void insertShouldReturnProductDto() {
		// arrange
		ProductDto productDto = ProductFactory.createProductDto();
		productDto.setId(null);
		
		// action
		ProductDto result = service.insert(productDto);
		
		// assert
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() {
		// arrange
		//long existingId = 1L;
		//ProductDto productDto = ProductFactory.createProductDto();
		
		// action
		ProductDto result = service.update(existingId, productDto);
		
		//assert
		Assertions.assertNotNull(result);
		Mockito.verify(productRepository, Mockito.times(1)).save(product);
		Mockito.verify(productRepository, Mockito.times(1)).getById(existingId);
		Mockito.verify(categoryRepository, Mockito.times(1)).getById(existingCategoryId);
	}
	
	@Test
	public void updateShouldThrowsServiceNotFoundExceptionWhenIdDoesNotExist() {
		// arrange
		//long existingId = 1L;
		//ProductDto productDto = ProductFactory.createProductDto();
		
		// assert
		Assertions.assertThrows(ServiceNotFoundException.class, () -> {
			// action
			service.update(nonExistingId, productDto);
		});
		
		//assert
		Mockito.verify(productRepository, Mockito.times(1)).getById(nonExistingId);
		Mockito.verify(productRepository, Mockito.never()).save(product);
		Mockito.verify(categoryRepository, Mockito.never()).getById(existingCategoryId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		// arrange
		//long existingId = 1L;
		
		// assert
		Assertions.assertDoesNotThrow(() -> {
			// action
			service.delete(existingId);
		});
		// verifica se "deleteById" do Mock productRepository foi chamado apenas 1 vez
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowsServiceNotFoundExceptionWhenIdDoesNotExist() {
		// arrange
		//long existingId = 1L;
		
		// assert
		Assertions.assertThrows(ServiceNotFoundException.class, () -> {
			// action
			service.delete(nonExistingId);
		});
		// verifica se "deleteById" do Mock productRepository realmente não foi chamado
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowsServiceDataBaseExceptionWhenDependentId() {
		// arrange
		//long existingId = 1L;
		
		// assert
		Assertions.assertThrows(ServiceDataBaseException.class, () -> {
			// action
			service.delete(dependentId);
		});
		// verifica se "deleteById" do Mock productRepository realmente não foi chamado
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
	}
	
}
