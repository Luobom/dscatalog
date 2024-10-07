package com.w3lsolucoes.dscatalog.repositories.factories;

import com.w3lsolucoes.dscatalog.dto.ProductDTO;
import com.w3lsolucoes.dscatalog.entities.Category;
import com.w3lsolucoes.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good phone", 800.0, "https://img.com/img.png", Instant.parse("2024-10-07T18:49:00Z"), null);
        product.getCategories().add(createCategory());
        return product;
    }

    public static Category createCategory() {
        return new Category(2L, "Eletrônicos");
    }


    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product);
    }


}
