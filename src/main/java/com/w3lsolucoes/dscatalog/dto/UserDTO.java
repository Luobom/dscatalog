package com.w3lsolucoes.dscatalog.dto;

import com.w3lsolucoes.dscatalog.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDTO(
        Long id,

        @NotEmpty(message = "Campo obrigatório")
        String firstName,
        String lastName,

        @Email(message = "Favor entrar um email válido")
        String email,
        Set<RoleDTO> roles

) {

    public UserDTO(User entity) {
        this(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet()));
    }

}
