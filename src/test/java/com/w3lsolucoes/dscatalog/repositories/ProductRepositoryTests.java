package com.w3lsolucoes.dscatalog.repositories;

import com.w3lsolucoes.dscatalog.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        // Arrange
        long existingId = 1L;

        // Act
        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        // Assert
        assertFalse(result.isPresent());
    }


}
