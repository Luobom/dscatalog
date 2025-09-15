package com.w3lsolucoes.dscatalog.repositories;

import com.w3lsolucoes.dscatalog.entities.Product;
import com.w3lsolucoes.dscatalog.projections.ProductCategoryProjection;
import com.w3lsolucoes.dscatalog.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT obj FROM Product obj WHERE LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> searchByName(String name, Pageable pageable);

    @Query(value = """
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN p.categories c
    WHERE (:categoryIds IS NULL OR c.id IN :categoryIds)
    AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """,
            countQuery = """
    SELECT COUNT(DISTINCT p)
    FROM Product p
    LEFT JOIN p.categories c
    WHERE (:categoryIds IS NULL OR c.id IN :categoryIds)
    AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    Page<ProductCategoryProjection> searchProducts(@Param("categoryIds") List<Long> categoryIds,
                                                   @Param("name") String name,
                                                   Pageable pageable);

}
