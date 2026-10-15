package com.springboot.app.apiminimarket.service;

import com.springboot.app.apiminimarket.dto.RolResponse;
import com.springboot.app.apiminimarket.mapper.RolMapper;
import com.springboot.app.apiminimarket.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {
    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    public RolService(RolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    public List<RolResponse> listarRoles(){
        return rolRepository.findAll().stream().map(rolMapper::toResponse).toList();
    }
}
