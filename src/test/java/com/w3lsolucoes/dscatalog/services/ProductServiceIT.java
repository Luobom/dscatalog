package com.w3lsolucoes.dscatalog.services;

import com.w3lsolucoes.dscatalog.dto.ProductMinDTO;
import com.w3lsolucoes.dscatalog.repositories.ProductRepository;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);

        // Act
        Page<ProductMinDTO> result = service.findAllPaged(pageRequest);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(25, result.getTotalElements());

    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(50, 10);

        // Act
        Page<ProductMinDTO> result = service.findAllPaged(pageRequest);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        // Act
        Page<ProductMinDTO> result = service.findAllPaged(pageRequest);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("Macbook Pro", result.getContent().get(0).name());
        assertEquals("PC Gamer", result.getContent().get(1).name());
        assertEquals("PC Gamer Alfa", result.getContent().get(2).name());
    }


    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        // Arrange

        // Act
        service.delete(existingId);

        // Assert
        assertFalse(repository.existsById(existingId));
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }


}
