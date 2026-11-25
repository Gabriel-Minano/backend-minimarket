package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.ReporteVentasResponse;
import com.springboot.app.apiminimarket.service.ReporteService;
import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ext.javatime.DateTimeParseException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    //GET /api/reportes/ventas?fechaInicio=2026-11-01&fechaFin=2026-11-25
    @GetMapping("/ventas")
    public ResponseEntity<ApiResponse<ReporteVentasResponse>> reporteVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {

        ReporteVentasResponse reporte = reporteService.obtenerReporte(
                convertirFecha(fechaInicio), convertirFecha(fechaFin));
        return ResponseEntity.ok(new ApiResponse<>(true, "Reporte generado correctamente", reporte));
    }

    //GET /api/reportes/ventas/exportar?fechaInicio=2026-11-01&fechaFin=2026-11-25
    @GetMapping("/ventas/exportar")
    public ResponseEntity<byte[]> exportarVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {

        LocalDate inicio = convertirFecha(fechaInicio);
        LocalDate fin = convertirFecha(fechaFin);
        byte[] archivo = reporteService.exportarCsv(inicio, fin);
        String nombreArchivo = "reporte-ventas-" + fechaInicio + "-a-" + fechaFin + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(archivo);
    }

    private LocalDate convertirFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("Debes indicar ambas fechas en formato yyyy-MM-dd.");
        }

        try {
            return LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Las fechas deben tener el formato yyyy-MM-dd.");
        }
    }

    //Mantiene el mensaje de validación en el mismo formato ApiResponse del proyecto.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> manejarRangoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

}
