package com.springboot.app.apiminimarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleVentaRequest(
        @NotNull(message = "El id del producto es obligatorio")
        @Positive(message = "El id del producto debe ser positivo")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima debe ser 1")
        Integer cantidad
) {
}
