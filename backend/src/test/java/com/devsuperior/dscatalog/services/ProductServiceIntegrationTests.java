package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ServiceNotFoundException;

@SpringBootTest //carrega o contexto da aplicação sem o servidor
@Transactional //garante o rollback das transação com o banco
public class ProductServiceIntegrationTests {
	
	@Autowired
	ProductService productService;
	
	@Autowired
	ProductRepository productRepository;
	
	// variáveis auxiliares
	private long existingId;	
	private long nonExistingId;
	//private long dependentId;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingId = 1L;
		nonExistingId = 30L;
		//dependentId = 3;
		countTotalProducts= 25;
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size12() {
		// arrange
		Pageable page = PageRequest.of(0, 12);
		
		// action
		Page<ProductDto> result = productService.findAllPaged(page);
		
		// assert
		Assertions.assertTrue(result.hasContent());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(12, result.getSize());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findAllPagedShouldReturnSortesPageWhenSortByName() {
		// arrange
		Pageable page = PageRequest.of(0, 12, Sort.by("name"));
		
		// action
		Page<ProductDto> result = productService.findAllPaged(page);
		
		// assert
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
		// arrange
		Pageable page = PageRequest.of(50, 12);
		
		// action
		Page<ProductDto> result = productService.findAllPaged(page);
		
		// assert
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() {
		
		// action
		ProductDto result = productService.findById(existingId);
		
		// assert
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals("The Lord of the Rings", result.getName());
	}
	
	@Test
	public void findByIdShouldThrowsServiceNotFoundExceptionWhenIdDoesNotExist() {
		
		// assert
		Assertions.assertThrows(ServiceNotFoundException.class, () -> {
			// action
			productService.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnProductDto() {
		// arrange
		ProductDto productDto = ProductFactory.createProductDto();
		productDto.setId(null);
		
		// action
		ProductDto result = productService.insert(productDto);
		
		// assert
		Assertions.assertNotNull(result);
		Assertions.assertEquals(26L, result.getId());
		Assertions.assertEquals("Phone", result.getName());
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() {
		// arrange
		ProductDto productDto = ProductFactory.createProductDto();
		productDto.setName("New Name");;
		
		// action
		ProductDto result = productService.update(existingId, productDto);
		
		// assert
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1L, result.getId());
		Assertions.assertEquals("New Name", result.getName());
	}
	
	@Test
	public void deleteShouldDeleteProductWhenIdExists() {
		// arrange
		//countTotalProducts= 25;
		
		// action
		productService.delete(existingId);
		
		// assert
		Assertions.assertEquals(countTotalProducts - 1, productRepository.count());	
	}
	
	@Test
	public void updateShouldThrowsServiceNotFoundExceptionWhenIdDoesNotExist() {
		// arrange
		ProductDto productDto = ProductFactory.createProductDto();
		
		// assert
		Assertions.assertThrows(ServiceNotFoundException.class, () -> {
			// action
			productService.update(nonExistingId, productDto);
		});
	}
	
	@Test
	public void deleteShouldReturnServiceNotFoundExceptionWhenIdDoesNotExist() {
		// arrange
		//countTotalProducts= 25;
		
		// assert
		Assertions.assertThrows(ServiceNotFoundException.class, () -> {
			// action
			productService.delete(nonExistingId);
		});
	}
	
//	@Test
//	@Transactional(propagation = Propagation.NEVER) 
//	public void deleteShouldReturnServiceDataBaseExceptionWhenIdDoesNotExist() {
//		// arrange
//		//countTotalProducts= 25;
//		
//		// assert
//		Assertions.assertThrows(ServiceDataBaseException.class, () -> {
//			// action
//			productService.delete(dependentId);
//		});
//	}

}
