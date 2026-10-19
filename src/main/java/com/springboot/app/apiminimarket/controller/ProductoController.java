package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.ProductoRequest;
import com.springboot.app.apiminimarket.dto.ProductoResponse;
import com.springboot.app.apiminimarket.service.ProductoService;
import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    //Ejemplos de uso
    //GET /api/productos
    //GET /api/productos?categoriaId=2
    //GET /api/productos?nombre=Inka
    //GET /api/productos?activo=false?nombre=Coca-Cola
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductoResponse>>> listarOFiltrarProductos(
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 15, sort = "id") Pageable pageable) {

        Page<ProductoResponse> productos;

        if (categoriaId != null) {
            productos = productoService.listarPorCategoria(categoriaId, activo, pageable);
        } else if (nombre != null && !nombre.isBlank()) {
            productos = productoService.buscarPorNombre(nombre, activo, pageable);
        } else {
            productos = productoService.listarProductos(activo, pageable);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Éxito al obtener productos", productos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtenerProductoPorId(@PathVariable Long id) {
        ProductoResponse producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Producto encontrado exitosamente", producto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crearProducto(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse productoCreado = productoService.crearProducto(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productoCreado.id())
                .toUri();
        return ResponseEntity.created(location).body(new ApiResponse<>(
                true, "Producto registrado exitosamente", productoCreado
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse productoActualizado = productoService.actualizarProducto(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Producto actualizado exitosamente", productoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Producto desactivado exitosamente", null));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ApiResponse<ProductoResponse>> reactivarProducto(
            @PathVariable  Long id) {
        ProductoResponse producto = productoService.reactivarProducto(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Producto reactivado exitosamente", producto));
    }

}
