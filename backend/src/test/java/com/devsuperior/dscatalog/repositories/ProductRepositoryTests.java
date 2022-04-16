package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.ProductFactory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository productRepository;
	
	//utiliza os dados do arquivo import.sql utilizado para o seed no Banco H2
	private long existingId;	
	private long nonExistingId;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 30L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void findByIdShouldReturnNonEmptyOptionalObjectWhenIdExists() {
		// arrange
		//long existingId = 1L; //utiliza o seed no Banco H2
		
		// action
		Optional<Product> result = productRepository.findById(existingId);
		
		// assert
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalObjectWhenIdDoesNotExist() {
		// arrange
		//long existingId = 1L; //utiliza o seed no Banco H2
		
		// action
		Optional<Product> result = productRepository.findById(nonExistingId);
		
		// assert
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		// arrange
		Product product = ProductFactory.createProduct();
		product.setId(null);
		
		// action
		productRepository.save(product);
		
		// assert
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}
	
	@Test
	public void saveShouldKeepUpdateDescriptionWhenIdIsTheSameBetweenObjectAndNewObject() {
		// arrange
		String newDescription = "Bad Phone";
		Product product = ProductFactory.createProduct();
		
		// action
		Product newProduct = productRepository.getById(product.getId());
		newProduct.setDescription(newDescription);
		productRepository.save(newProduct);
		
		// assert
		Assertions.assertTrue(product.getId() == newProduct.getId());
		Assertions.assertFalse(product.getDescription() == newProduct.getDescription());
		Assertions.assertEquals("Bad Phone", newProduct.getDescription());	
	}
	
	@Test
	public void deleteShouldDeleteObjectByIdWhenIdExists() {
		// arrange
		//long existingId = 1L; //utiliza o seed no Banco H2
		
		// action
		productRepository.deleteById(existingId);
		Optional<Product> result = productRepository.findById(existingId);
		
		// assert
		Assertions.assertTrue(result.isEmpty());	
	}
	
	@Test
	public void deleteShouldThrowsEmptyResultDataAccessExceptionWhenIdNotExists() {
		// arrange
		//long nonExistingId = 30L; //utiliza o seed no Banco H2
		
		// assert
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			// action
			productRepository.deleteById(nonExistingId);
		});
	}

}
