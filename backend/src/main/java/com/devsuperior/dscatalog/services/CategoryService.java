package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ServiceNotFoundException;

@Service
public class CategoryService {
	
  /*//Injeção de Dependencia Manual via Construtor
	private CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}*/
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<CategoryDto> findAll() {
		List<Category> categoriesDto = categoryRepository.findAll();
		return categoriesDto.stream().map(x -> new CategoryDto(x)).toList();
	}
	
	@Transactional(readOnly = true)
	public CategoryDto findById(Long id) {
		Optional<Category> obj = categoryRepository.findById(id);
		Category category = obj.orElseThrow(() -> new ServiceNotFoundException("Entity Not Found!"));
		return new CategoryDto(category);
	}
	
	@Transactional
	public CategoryDto insert(CategoryDto categoryDto) {
		Category category = new Category();
		category.setName(categoryDto.getName());
		category = categoryRepository.save(category);
		return new CategoryDto(category);	
	}

	@Transactional
	public CategoryDto update(Long id, CategoryDto categoryDto) {
		try {
			Category newCategory = categoryRepository.getById(id); //cria apenas uma referencia da Entidade
			newCategory.setName(categoryDto.getName());
			newCategory = categoryRepository.save(newCategory);	
			return new CategoryDto(newCategory);
		} catch (EntityNotFoundException e) {
			throw new ServiceNotFoundException("ID [5] Not Found!");
		}
	}
	
}
