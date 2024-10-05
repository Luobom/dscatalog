package com.w3lsolucoes.dscatalog.services;

import com.w3lsolucoes.dscatalog.dto.CategoryDTO;
import com.w3lsolucoes.dscatalog.entities.Category;
import com.w3lsolucoes.dscatalog.repositories.CategoryRepository;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    // CategoryRepository constructor injection
    public final CategoryRepository repository;
    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> categories = repository.findAll();
        return categories.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        // 1) Optional is a container object used to contain not-null objects.
        // Optional<Category> obj = repository.findById(id);
        // Category category = obj.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // 2) The orElseThrow() method returns the value if present, otherwise throws NoSuchElementException.
        Category category = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.name());
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    }

}
