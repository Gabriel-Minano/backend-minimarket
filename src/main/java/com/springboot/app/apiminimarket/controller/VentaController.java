package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.VentaRequest;
import com.springboot.app.apiminimarket.dto.VentaResponse;
import com.springboot.app.apiminimarket.service.VentaService;
import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    //Filtrado por fecha empieza desde la útima
    //GET /api/ventas?page=0&size=10
    //GET /api/ventas?activo=false
    @GetMapping
    public ResponseEntity<ApiResponse<Page<VentaResponse>>> listarVentas(
            @RequestParam(defaultValue = "true") boolean activo,
            @PageableDefault(size = 15, sort = "fechaVenta", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VentaResponse> ventas = ventaService.listarVentas(activo, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Éxito al obtener las ventas", ventas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaResponse>> obtenerVentaPorId(@PathVariable Long id) {
        VentaResponse venta = ventaService.obtenerPorId(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Venta encontrada exitosamente", venta));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VentaResponse>> registrarVenta(@Valid @RequestBody VentaRequest request) {
        VentaResponse ventaRegistrada = ventaService.registrarVenta(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ventaRegistrada.idVenta())
                .toUri();

        return ResponseEntity.created(location).body(new ApiResponse<>(
                true, "Venta registrada exitosamente", ventaRegistrada
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> anularVenta(@PathVariable Long id) {
        ventaService.anularVenta(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Venta anulada exitosamente y stock revertido", null));
    }
}
