package com.w3lsolucoes.dscatalog.repositories;

import com.w3lsolucoes.dscatalog.entities.Product;
import com.w3lsolucoes.dscatalog.factories.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        // Arrange

        // Act
        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        // Arrange
        Product product = Factory.createProduct();

        // Act
        product.setId(null);
        product = repository.save(product);

        // Assert
        assertNotNull(product.getId());
        assertEquals(countTotalProducts + 1, product.getId());

    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        // Arrange

        // Act
        Optional<Product> result = repository.findById(existingId);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        // Arrange

        // Act
        Optional<Product> result = repository.findById(nonExistingId);

        // Assert
        assertTrue(result.isEmpty());
    }

}
