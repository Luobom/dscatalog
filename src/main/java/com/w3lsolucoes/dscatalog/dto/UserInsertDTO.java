package com.w3lsolucoes.dscatalog.dto;

import com.w3lsolucoes.dscatalog.entities.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserInsertDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        Set<RoleDTO> roles
) {

    public UserInsertDTO(User entity) {
        this(entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getPassword(), entity.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet()));
    }

}
