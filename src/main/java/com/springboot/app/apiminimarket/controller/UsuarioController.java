package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.UsuarioRequest;
import com.springboot.app.apiminimarket.dto.UsuarioResponse;
import com.springboot.app.apiminimarket.dto.UsuarioUpdateRequest;
import com.springboot.app.apiminimarket.service.UsuarioService;
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario encontrado exitosamente", usuario));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuarioCreado = usuarioService.crearUsuario(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioCreado.id())
                .toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponse<>(true, "Usuario registrado exitosamente", usuarioCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        UsuarioResponse usuarioActualizado = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario actualizado exitosamente", usuarioActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario desactivado exitosamente", null));
    }
}
