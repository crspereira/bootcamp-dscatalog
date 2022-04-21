package com.devsuperior.dscatalog.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ServiceDataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ServiceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class) //carrega o contexto, porém somente da camada web 
public class ProductControllerTests {
	
	@Autowired
	private MockMvc mockMvc; //chama os endpoint. simula as requisições
	
	@Autowired
	private ObjectMapper objectMapper; //transforam objetos Java em Json
	
	@MockBean
	private ProductService productService; //mock o productService
	
	@MockBean
	private ProductRepository productRepository; //mock o productService
	
	// variáveis auxiliares
	private long existingId;	
	private long nonExistingId;
	private long dependentId;
	private ProductDto productDto;
	private PageImpl<ProductDto> page; //instância fake de uma página
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingId = 1L;
		nonExistingId = 30L;
		dependentId=3L;
		productDto = ProductFactory.createProductDto();
		page = new PageImpl<>(List.of(productDto)); //uma página fake com a lista de um produto nela
		
		// configura ação simulada do comportamento "findAll" para o Mock productService
		Mockito.when(productService.findAllPaged(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
		
		// configura ação simulada do comportamento "findById" para o Mock productService
		Mockito.when(productService.findById(existingId)).thenReturn(productDto);
		Mockito.when(productService.findById(nonExistingId)).thenThrow(ServiceNotFoundException.class);
		
		// configura ação simulada do comportamento "insert" para o Mock productService
		Mockito.when(productService.insert(any())).thenReturn(productDto);
		
		// configura ação simulada do comportamento "update" para o Mock productService
		Mockito.when(productService.update(eq(existingId), any())).thenReturn(productDto);
		Mockito.when(productService.update(eq(nonExistingId), any())).thenThrow(ServiceNotFoundException.class);
		
		// configura ação simulada do comportamento "Delete" para o Mock productService
		Mockito.doNothing().when(productService).delete(existingId);
		Mockito.doThrow(ServiceNotFoundException.class).when(productService).delete(nonExistingId);
		Mockito.doThrow(ServiceDataBaseException.class).when(productService).delete(dependentId);	
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		//action e assert
		//mockMvc.perform(get("/products")).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		// arrange
		//existingId = 1L;
		
		// action
		ResultActions result = mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() throws Exception {
		// arrange
		//existingId = 1L;
		
		// action
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		// arrange
		//existingId = 1L;
		
		// action
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.status").exists());
	}
	
	@Test
	public void insertShouldReturnProductDtoCreated() throws Exception {
		// arrange
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		// action
		ResultActions result = mockMvc.perform(post("/products/")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldReturnProductDtoWheIdExixts() throws Exception {
		// arrange
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		// action
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWheIdDoesNotExixt() throws Exception {
		// arrange
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		// action
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.status").exists());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		// arrange
		//existingId = 1L;
		
		// action
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		// arrange
		//existingId = 1L;
		
		// action
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.status").exists());
	}
	
	@Test
	public void deleteShouldReturnBadRequestWhenIdDoesNotExist() throws Exception {
		// arrange
		//existingId = 1L;
		
		// action
		ResultActions result = mockMvc.perform(delete("/products/{id}", dependentId)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isBadRequest());
		result.andExpect(jsonPath("$.status").exists());
	}

}
