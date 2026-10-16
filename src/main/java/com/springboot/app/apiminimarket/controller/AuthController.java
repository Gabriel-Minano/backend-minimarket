package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.dto.AuthResponse;
import com.springboot.app.apiminimarket.dto.LoginRequest;
import com.springboot.app.apiminimarket.security.JwtService;
import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request){
        //Autenticar credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        //En caso pase cargar detalles y generar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new ApiResponse<>(true, "Login exitoso", new AuthResponse(token)));
    }

}
