package com.springboot.app.apiminimarket.dto;

public record CategoriaResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean activo
) {
}
