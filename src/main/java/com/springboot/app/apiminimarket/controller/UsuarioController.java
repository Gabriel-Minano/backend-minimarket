package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.UsuarioResponse;
import com.springboot.app.apiminimarket.service.UsuarioService;
import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UsuarioResponse>>> paginarUsuarios(
            @RequestParam(defaultValue = "true") boolean activo,
            @PageableDefault(size = 15, sort = "id")Pageable pageable){
        Page<UsuarioResponse> usuarios = usuarioService.listarUsuarios(activo, pageable);
        ApiResponse<Page<UsuarioResponse>> response = new ApiResponse<>(
                true, "Éxito al obtener usuarios", usuarios
        );
        return ResponseEntity.ok(response);
    }
}
