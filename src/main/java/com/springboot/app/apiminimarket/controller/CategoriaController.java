package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.CategoriaRequest;
import com.springboot.app.apiminimarket.dto.CategoriaResponse;
import com.springboot.app.apiminimarket.service.CategoriaService;
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
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> paginarCategorias(
            @RequestParam(defaultValue = "true") boolean activo,
            @PageableDefault(size = 15, sort = "id") Pageable pageable){
        Page<CategoriaResponse> categorias = categoriaService.listarCategorias(activo, pageable);
        ApiResponse<Page<CategoriaResponse>> response = new ApiResponse<>(
                true, "Éxito al obtener categorías", categorias
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerCategoriaPorId(
            @PathVariable Long id){
        CategoriaResponse categoria = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,"Categoría encontrada exitosamente", categoria));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> crearCategoria(@Valid @RequestBody CategoriaRequest request){
        CategoriaResponse categoriaCreada = categoriaService.crearCategoria(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoriaCreada.id())
                .toUri();
        return ResponseEntity.created(location).body(new ApiResponse<>(
                true, "Categoría registrada exitosamente", categoriaCreada
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request){
        CategoriaResponse categoriaActualizada = categoriaService.actualizarCategoria(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Categoría actualizada exitosamente", categoriaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivarCategoria(
            @PathVariable Long id){
        categoriaService.desactivarCategoria(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Categoría desactivada exitosamente", null));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ApiResponse<CategoriaResponse>> reactivarCategoria(
            @PathVariable  Long id){
        CategoriaResponse categoria = categoriaService.reactivarCategoria(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Categoría reactivada exitosamente", categoria));
    }

}
