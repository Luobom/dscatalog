package com.w3lsolucoes.dscatalog.services;

import com.w3lsolucoes.dscatalog.dto.ProductDTO;
import com.w3lsolucoes.dscatalog.dto.ProductMinDTO;
import com.w3lsolucoes.dscatalog.entities.Product;
import com.w3lsolucoes.dscatalog.utils.Factory;
import com.w3lsolucoes.dscatalog.repositories.ProductRepository;
import com.w3lsolucoes.dscatalog.services.exceptions.DataBaseException;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl page;
    private Product product;
    private ProductDTO productDTO;
    // for searchByName
    private String name;
    private String inexistentName;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 5000L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = Factory.createProduct();
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));
        name = "Phone";
        inexistentName = "Camera";

        // Obs : O método findAll() retorna um objeto do tipo Page.
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        // Obs : O método save() retorna um objeto do tipo Product.
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        // Obs : o metodo findById() com existingId retorna um Optional, que é um container que pode ou não conter um valor não nulo.
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        // Obs : O método findById() com nonExistingId retorna um Optional vazio.
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        // simulate getReferenceById
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        // searchByName
        Mockito.when(repository.searchByName(name, PageRequest.of(0, 10))).thenReturn(page);
        Mockito.when(repository.searchByName(inexistentName, PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of()));

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.when(repository.existsById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    // searchByName - by Lu
    @Test
    public void searchByNameShouldReturnPage() {
        var result = service.searchByName(name, PageRequest.of(0, 10));
        assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).searchByName(name, PageRequest.of(0, 10));
    }

    @Test
    public void searchByNameShouldReturnEmptyPageWhenNameDoesNotExists() {
        var result = service.searchByName(inexistentName, PageRequest.of(0, 10));
        assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).searchByName(inexistentName, PageRequest.of(0, 10));
    }


    // findById
    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        var result = service.findById(existingId);
        assertNotNull(result);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }


    // save  --- by Lu
    @Test
    public void saveShouldReturnProductDTO() {
        var result = service.save(productDTO);
        assertNotNull(result);
    }


    // update
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        var result = service.update(existingId, productDTO);
        assertNotNull(result);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, productDTO);
        });
    }


    // findAllPaged by chatGPT
    @Test
    public void findAllPagedShouldReturnPage() {
        var result = service.findAllPaged(Pageable.unpaged());
        assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(Pageable.unpaged());
    }

    // findAllPaged by Nelio Alves Course
    @Test
    public void findAllPagedShouldReturnPageOLD() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductMinDTO> result = service.findAllPaged(pageable);

        assertNotNull(result);
        Mockito.verify(repository).findAll(pageable);
    }


    // delete
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        assertThrows(DataBaseException.class, () -> {
            service.delete(dependentId);
        });
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
