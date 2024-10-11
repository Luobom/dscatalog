package com.w3lsolucoes.dscatalog.dto;

import com.w3lsolucoes.dscatalog.entities.Role;

public record RoleDTO(
        Long id,
        String authority
) {

    public RoleDTO(Role entity) {
        this(entity.getId(), entity.getAuthority());
    }

}
