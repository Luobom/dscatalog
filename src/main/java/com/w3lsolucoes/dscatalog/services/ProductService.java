package com.w3lsolucoes.dscatalog.services;

import com.w3lsolucoes.dscatalog.dto.CategoryDTO;
import com.w3lsolucoes.dscatalog.dto.ProductDTO;
import com.w3lsolucoes.dscatalog.dto.ProductMinDTO;
import com.w3lsolucoes.dscatalog.entities.Category;
import com.w3lsolucoes.dscatalog.entities.Product;
import com.w3lsolucoes.dscatalog.projections.ProductCategoryProjection;
import com.w3lsolucoes.dscatalog.projections.ProductProjection;
import com.w3lsolucoes.dscatalog.repositories.ProductRepository;

import com.w3lsolucoes.dscatalog.services.exceptions.DataBaseException;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<ProductCategoryProjection> findAllPaged(String name, String categoryId, Pageable pageable) {

        List<Long> idsLong = Arrays.stream(categoryId.split(",")).map(Long::parseLong).toList();

        List<Long> ids = idsLong.getFirst() == 0 ? null : idsLong;

        return repository.searchProducts(ids, name, pageable);

    }

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> searchByName(String name, Pageable pageable) {
        Page<Product>  products = repository.searchByName(name, pageable);
        return products.map(ProductMinDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO save(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        for (CategoryDTO catDTO : dto.categories()) {
            product.getCategories().add(new Category(catDTO.id(), catDTO.name()));
        }

        return new ProductDTO(repository.save(product));
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product product = repository.getReferenceById(id);
            String[] ignoredProperties = {"id"};
            BeanUtils.copyProperties(dto, product, ignoredProperties);

            product.getCategories().clear();
            for (CategoryDTO catDTO : dto.categories()) {
                product.getCategories().add(new Category(catDTO.id(), catDTO.name()));
            }

            return new ProductDTO(repository.save(product));
        } catch (EntityNotFoundException | FatalBeanException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new ResourceNotFoundException("Product not found for the given ID: " + id);
            }
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation - This Product has dependencies and cannot be deleted.");

        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Product not found for the given ID: " + id);
        }
    }


}
