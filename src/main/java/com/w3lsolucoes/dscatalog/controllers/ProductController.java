package com.w3lsolucoes.dscatalog.controllers;

import com.w3lsolucoes.dscatalog.dto.ProductDTO;
import com.w3lsolucoes.dscatalog.dto.ProductMinDTO;
import com.w3lsolucoes.dscatalog.projections.ProductProjection;
import com.w3lsolucoes.dscatalog.services.ProductService;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

//    @GetMapping
//    public ResponseEntity<Page<ProductMinDTO>> findAll(Pageable pageable) {
//        return ResponseEntity.status(HttpStatus.OK).body(service.findAllPaged(pageable));
//    }

    @GetMapping
    public ResponseEntity<Page<ProductProjection>> findAll(

            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "categoryId", defaultValue = "0") String categoryId,

            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAllPaged(name, categoryId, pageable));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<ProductMinDTO>> searchByName(@RequestParam String name, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(service.searchByName(name, pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @PostMapping
    public ResponseEntity<ProductDTO> save(@Valid @RequestBody ProductDTO dto) {
        ProductDTO productDTO = service.save(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(productDTO.id()).toUri();
        return ResponseEntity.created(uri).body(productDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // modo diferente de retornar o status 204 em uma chamada void
    public void delete(@PathVariable Long id) {
        try {
            service.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
