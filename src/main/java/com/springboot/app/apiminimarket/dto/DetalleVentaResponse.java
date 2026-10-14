package com.springboot.app.apiminimarket.dto;

import java.math.BigDecimal;

public record DetalleVentaResponse(
        Long idDetalleVenta,
        Long idProducto,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
