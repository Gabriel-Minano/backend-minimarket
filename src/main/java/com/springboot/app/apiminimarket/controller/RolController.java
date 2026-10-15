package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.RolResponse;
import com.springboot.app.apiminimarket.service.RolService;
import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolResponse>>> listarRoles(){
        return ResponseEntity.ok(new ApiResponse<>(true, "Éxito al obtener roles", rolService.listarRoles()));
    }
}
