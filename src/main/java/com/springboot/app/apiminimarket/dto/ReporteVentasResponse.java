package com.springboot.app.apiminimarket.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteVentasResponse(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Long cantidadVentas,
        Long unidadesVendidas,
        BigDecimal totalVendido,
        List<ReporteVentaDiaResponse> ventasPorDia
) {
}
