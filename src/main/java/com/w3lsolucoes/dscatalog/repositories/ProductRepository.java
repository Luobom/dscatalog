package com.w3lsolucoes.dscatalog.repositories;

import com.w3lsolucoes.dscatalog.entities.Product;
import com.w3lsolucoes.dscatalog.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT obj FROM Product obj WHERE LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> searchByName(String name, Pageable pageable);


    // RAW SQL
    @Query(nativeQuery = true, value = """
            SELECT DISTINCT tb_product.id, tb_product.name
            FROM tb_product INNER JOIN tb_product_category ON (tb_product.id = tb_product_category.product_id)
            WHERE(:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY tb_product.name ASC
            """, countQuery = """
            SELECT COUNT(*) FROM (
            SELECT DISTINCT tb_product.id, tb_product.name
            FROM tb_product INNER JOIN tb_product_category ON (tb_product.id = tb_product_category.product_id)
            WHERE(:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY tb_product.name ASC
            ) AS result
            """)

//    // JPQL
//    @Query("SELECT p FROM Product p JOIN p.categories cats WHERE " +
//            "(:categoryIds IS NULL OR cats.id IN :categoryIds) AND " +
//            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
//            "ORDER BY p.name")
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);

}
