package com.springboot.app.apiminimarket.service;

import com.springboot.app.apiminimarket.dto.ReporteVentaDiaResponse;
import com.springboot.app.apiminimarket.dto.ReporteVentasResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {
    private final JdbcTemplate jdbcTemplate;

    public ReporteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Ventas vigentes agrupadas por día. El reporte trae como máximo un año de resultados diarios.
    @Transactional(readOnly = true)
    public ReporteVentasResponse obtenerReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        validarFechas(fechaInicio, fechaFin);

        String sql = """
                SELECT CAST(v.fecha_venta AS DATE) AS fecha,
                       COUNT(DISTINCT v.id_venta) AS cantidad_ventas,
                       COALESCE(SUM(d.cantidad), 0) AS unidades_vendidas,
                       COALESCE(SUM(d.subtotal), 0) AS total_vendido
                FROM venta v
                INNER JOIN detalle_venta d ON d.id_venta = v.id_venta
                WHERE v.activo = TRUE
                  AND v.fecha_venta >= ?
                  AND v.fecha_venta < ?
                GROUP BY CAST(v.fecha_venta AS DATE)
                ORDER BY fecha
                """;

        //Se usa el día siguiente como límite exclusivo para incluir la fecha final completa.
        List<ReporteVentaDiaResponse> ventasPorDia = jdbcTemplate.query(sql,
                (rs, fila) -> new ReporteVentaDiaResponse(
                        rs.getDate("fecha").toLocalDate(),
                        rs.getLong("cantidad_ventas"),
                        rs.getLong("unidades_vendidas"),
                        rs.getBigDecimal("total_vendido")
                ), fechaInicio.atStartOfDay(), fechaFin.plusDays(1).atStartOfDay());

        long cantidadVentas = 0;
        long unidadesVendidas = 0;
        BigDecimal totalVendido = BigDecimal.ZERO;

        for (ReporteVentaDiaResponse dia : ventasPorDia) {
            cantidadVentas += dia.cantidadVentas();
            unidadesVendidas += dia.unidadesVendidas();
            totalVendido = totalVendido.add(dia.totalVendido());
        }

        return new ReporteVentasResponse(
                fechaInicio,
                fechaFin,
                cantidadVentas,
                unidadesVendidas,
                totalVendido,
                ventasPorDia
        );
    }

    //Permite descargar el mismo reporte sin librerías externas.
    public byte[] exportarCsv(LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteVentasResponse reporte = obtenerReporte(fechaInicio, fechaFin);

        //BOM UTF-8 para que Excel reconozca las tildes y el carácter ñ.
        StringBuilder csv = new StringBuilder("\uFEFFFecha;Ventas;Unidades vendidas;Total (PEN)\r\n");

        for (ReporteVentaDiaResponse dia : reporte.ventasPorDia()) {
            csv.append(dia.fecha()).append(';')
                    .append(dia.cantidadVentas()).append(';')
                    .append(dia.unidadesVendidas()).append(';')
                    .append(dia.totalVendido().toPlainString()).append("\r\n");
        }

        csv.append("TOTAL;")
                .append(reporte.cantidadVentas()).append(';')
                .append(reporte.unidadesVendidas()).append(';')
                .append(reporte.totalVendido().toPlainString()).append("\r\n");

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Debes indicar la fecha de inicio y fin.");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial.");
        }

        //Hasta la misma fecha del año siguiente. plusYears respeta los años bisiestos.
        if (fechaFin.isAfter(fechaInicio.plusYears(1))) {
            throw new IllegalArgumentException("El rango de búsqueda no puede superar un año.");
        }
    }


}
