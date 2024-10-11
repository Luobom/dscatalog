package com.w3lsolucoes.dscatalog.dto;

import com.w3lsolucoes.dscatalog.entities.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<RoleDTO> roles

) {

    public UserDTO(User entity) {
        this(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet()));
    }

}
