package com.w3lsolucoes.dscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.w3lsolucoes.dscatalog.utils.Factory;
import com.w3lsolucoes.dscatalog.services.ProductService;
import com.w3lsolucoes.dscatalog.services.exceptions.DataBaseException;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(ProductController.class)

// desabilitar a segurança para testar o controlador
@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private ProductService service;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        // find's and search's
        when(service.findAllPaged(any())).thenReturn(new PageImpl<>(List.of(Factory.createProductMinDTO())));
        when(service.findById(existingId)).thenReturn(Factory.createProductDTO());
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        when(service.searchByName(any(), any())).thenReturn(new PageImpl<>(List.of(Factory.createProductMinDTO())));

        // insert, update and delete
        when(service.save(any())).thenReturn(Factory.createProductDTO());
        when(service.update(eq(existingId), any())).thenReturn(Factory.createProductDTO());
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DataBaseException.class).when(service).delete(dependentId);


    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.imgUrl").exists())
        ;
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void searchShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products/search")
                .param("name", "Phone")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }


    @Test
    public void insertShouldReturnProductDTOCreated() throws Exception {

        String jsonBody = jacksonObjectMapper.writeValueAsString(Factory.createProductDTO());

        mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.imgUrl").exists())
        ;
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        String jsonBody = jacksonObjectMapper.writeValueAsString(Factory.createProductDTO());

        mockMvc.perform(put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.imgUrl").exists())
        ;
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        String jsonBody = jacksonObjectMapper.writeValueAsString(Factory.createProductDTO());

        mockMvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
        ;
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }


}
