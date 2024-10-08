package com.w3lsolucoes.dscatalog.controllers;

import com.w3lsolucoes.dscatalog.factories.Factory;
import com.w3lsolucoes.dscatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @BeforeEach
    void setUp() throws Exception {
        when(service.findAllPaged(any())).thenReturn(new PageImpl<>(List.of(Factory.createProductMinDTO())));
//        when(service.searchByName(any(), any())).thenReturn(new PageImpl<>(List.of(Factory.createProductMinDTO())));
//        when(service.findById(existingId)).thenReturn(Factory.createProductDTO());
//        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
//        when(service.save(ArgumentMatchers.any())).thenReturn(Factory.createProductDTO());
//        when(service.update(eq(existingId), ArgumentMatchers.any())).thenReturn(Factory.createProductDTO());
//        when(service.update(eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

}
