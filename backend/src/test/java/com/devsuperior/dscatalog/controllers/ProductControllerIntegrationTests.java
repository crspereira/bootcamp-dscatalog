package com.devsuperior.dscatalog.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest			//carrega o contexto da aplicação.
@AutoConfigureMockMvc	//trata as requisições sem subir o servidor web.
@Transactional			//garante o rollback a cada transação com o DB.
public class ProductControllerIntegrationTests {
	
	@Autowired
	private MockMvc mockMvc; //chama os endpoint. simula as requisições
	
	@Autowired
	private ObjectMapper objectMapper; //transforam objetos Java em Json
	
	// variáveis auxiliares
	private long existingId;	
	private long nonExistingId;
	//private long dependentId = 3L;
	private long countTotalProducts;
	private ProductDto productDto;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingId = 1L;
		nonExistingId = 30L;
		//dependentId = 3L;
		countTotalProducts= 25L;
		productDto = ProductFactory.createProductDto();
	}
	
	@Test
	public void findAllPagedShouldSortedWhenSortByName() throws Exception {
		
		// action
		ResultActions result = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
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
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value("The Lord of the Rings"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		// arrange
		//nonExistingId = 30L;
		
		// action
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.status").value("404"));
		result.andExpect(jsonPath("$.error").value("Entity Not Found!"));
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
		result.andExpect(jsonPath("$.id").value(countTotalProducts + 1));
		result.andExpect(jsonPath("$.name").value("Phone"));
	}
	
	@Test
	public void updateShouldReturnProductDtoWheIdExists() throws Exception {
		// arrange
		//existingId = 1L;
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		// action
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(productDto.getName()));
		result.andExpect(jsonPath("$.description").value(productDto.getDescription()));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		// arrange
		//nonExistingId = 30L;
		String jsonBody = objectMapper.writeValueAsString(productDto);
		
		// action
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// assert
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.status").value("404"));
		result.andExpect(jsonPath("$.error").value("ID ["+ nonExistingId +"] Not Found!"));
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
		result.andExpect(jsonPath("$.status").value("404"));
		result.andExpect(jsonPath("$.error").value("ID ["+ nonExistingId +"] Not Found!"));
	}
	
//	@Test
//	@Transactional(propagation = Propagation.NEVER) 
//	public void deleteShouldReturnBadRequestWhenIdDoesNotExist() throws Exception {
//		// arrange
//		//existingId = 1L;
//		
//		// action
//		ResultActions result = mockMvc.perform(delete("/products/{id}", dependentId)
//				.accept(MediaType.APPLICATION_JSON));
//		
//		// assert
//		result.andExpect(status().isBadRequest());
//		result.andExpect(jsonPath("$.status").value("400"));
//		result.andExpect(jsonPath("$.error").value("DataBase Integrity Violation!"));
//	}
}
