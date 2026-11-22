package com.springboot.app.apiminimarket.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
public record VentaRequest(
        @NotEmpty(message = "La venta debe contener al menos un producto")
        List<DetalleVentaRequest> detalles
) {
}
