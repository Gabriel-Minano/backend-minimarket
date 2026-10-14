package com.springboot.app.apiminimarket.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
public record VentaRequest(
        @NotNull(message = "El id del usuario es obligatorio")
        @Positive(message = "El id del usuario debe ser positivo")
        Long idUsuario,

        @NotEmpty(message = "La venta debe contener al menos un producto")
        List<DetalleVentaRequest> detalles
) {
}
