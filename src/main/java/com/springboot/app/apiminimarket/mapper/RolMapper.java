package com.springboot.app.apiminimarket.mapper;

import com.springboot.app.apiminimarket.dto.RolResponse;
import com.springboot.app.apiminimarket.model.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {
    public RolResponse toResponse(Rol rol){
        if(rol == null) return null;
        return new RolResponse(rol.getId(), rol.getNombre());
    }
}
