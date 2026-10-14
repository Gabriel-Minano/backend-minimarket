package com.springboot.app.apiminimarket.dto;

import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        boolean activo,
        Set<RolResponse> roles
) {
}
