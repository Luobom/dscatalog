package com.w3lsolucoes.dscatalog.repositories;

import com.w3lsolucoes.dscatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
