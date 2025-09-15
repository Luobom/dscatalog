package com.w3lsolucoes.dscatalog.services;

import com.w3lsolucoes.dscatalog.projections.ProductCategoryProjection;
import com.w3lsolucoes.dscatalog.repositories.ProductRepository;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Garante que cada teste rode em uma transação e dê rollback no final
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L; // O número total de produtos no seu banco de dados de teste
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String name = "";
        String categoryId = "0";

        // Act
        Page<ProductCategoryProjection> result = service.findAllPaged(name, categoryId, pageable);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.getSize());
        assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
        // Arrange
        // Página 50, que não existe no seu banco de dados de 25 produtos
        Pageable pageable = PageRequest.of(50, 10);
        String name = "";
        String categoryId = "0";

        // Act
        // O service vai chamar o repository real, que vai consultar o banco de dados real
        Page<ProductCategoryProjection> result = service.findAllPaged(name, categoryId, pageable);

        // Assert
        // O resultado esperado da consulta ao banco é uma página vazia
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        String name = "";
        String categoryId = "0";

        // Act
        Page<ProductCategoryProjection> result = service.findAllPaged(name, categoryId, pageable);

        // Assert
        // IMPORTANTE: Essas asserções dependem dos dados exatos do seu import.sql
        assertFalse(result.isEmpty());
        assertEquals("Macbook Pro", result.getContent().get(0).getName());
        assertEquals("PC Gamer", result.getContent().get(1).getName());
        assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }


    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        // Act
        service.delete(existingId);

        // Assert
        // Verifica diretamente no repositório se o item foi removido do banco
        assertFalse(repository.existsById(existingId));
        assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange, Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }
}