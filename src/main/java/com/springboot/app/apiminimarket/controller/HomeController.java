package com.springboot.app.apiminimarket.controller;

import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> home(){
        return ResponseEntity.ok(
                new ApiResponse<>(true, "API Funcionando",null));
    }
}
