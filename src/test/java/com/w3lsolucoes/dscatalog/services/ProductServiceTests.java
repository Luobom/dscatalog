package com.w3lsolucoes.dscatalog.services;

import com.w3lsolucoes.dscatalog.repositories.ProductRepository;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;


    private long existingId;
    private long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.when(repository.existsById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void deleteDoNothingWhenIdExists() {
        assertDoesNotThrow(
                () -> service.delete(existingId)
        );

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(nonExistingId)
        );
    }

}
