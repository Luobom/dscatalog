package com.w3lsolucoes.dscatalog.repositories;

import com.w3lsolucoes.dscatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
