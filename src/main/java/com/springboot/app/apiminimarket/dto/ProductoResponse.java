package com.springboot.app.apiminimarket.dto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        Long idCategoria,
        boolean activo
) {
}
