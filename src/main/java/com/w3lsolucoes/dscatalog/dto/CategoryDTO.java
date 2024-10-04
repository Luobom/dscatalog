package com.w3lsolucoes.dscatalog.dto;

import com.w3lsolucoes.dscatalog.entities.Category;

public record CategoryDTO(
        Long id,
        String name
) {

    public CategoryDTO(Category entity) {
        this(entity.getId(), entity.getName());
    }

}
