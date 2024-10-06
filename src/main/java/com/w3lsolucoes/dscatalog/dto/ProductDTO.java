package com.w3lsolucoes.dscatalog.dto;

import com.w3lsolucoes.dscatalog.entities.Product;
import jakarta.validation.constraints.*;

import java.util.Set;
import java.util.stream.Collectors;

public record ProductDTO(
        Long id,
        @Size(min = 3, max = 80, message = "O nome do produto deve ter entre 3 e 80 caracteres")
        @NotBlank(message = "Campo obrigatório")
        String name,
        @Size(min = 10, message = "A descrição do produto deve ter no mínimo 10 caracteres")
        @NotBlank(message = "Campo obrigatório")
        String description,
        @NotNull(message = "Campo obrigatório")
        @Positive(message = "O preço deve ser um valor positivo")
        Double price,
        String imgUrl,

        @NotEmpty(message = "O produto deve ter pelo menos uma categoria")
        Set<CategoryDTO> categories

) {
    public ProductDTO(Product entity) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getImgUrl(),
                entity.getCategories().stream().map(CategoryDTO::new).collect(Collectors.toSet())
        );
    }
}
