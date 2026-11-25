package com.springboot.app.apiminimarket.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteVentaDiaResponse(
        LocalDate fecha,
        Long cantidadVentas,
        Long unidadesVendidas,
        BigDecimal totalVendido
) {
}