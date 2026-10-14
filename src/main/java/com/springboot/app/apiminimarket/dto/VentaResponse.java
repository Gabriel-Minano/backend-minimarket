package com.springboot.app.apiminimarket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long idVenta,
        Long idUsuario,
        String nombreUsuario,
        LocalDateTime fechaVenta,
        BigDecimal total,
        boolean activo,
        List<DetalleVentaResponse> detalles
) {
}
