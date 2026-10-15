package com.springboot.app.apiminimarket.mapper;

import com.springboot.app.apiminimarket.dto.RolResponse;
import com.springboot.app.apiminimarket.dto.UsuarioResponse;
import com.springboot.app.apiminimarket.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    //Convertir a response
    public UsuarioResponse toResponse(Usuario usuario){
        if(usuario == null) return null;

        var rolesResponse = usuario.getRoles().stream()
                .map(rol -> new RolResponse(rol.getId(), rol.getNombre()))
                .collect(Collectors.toSet());

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.isActivo(),
                rolesResponse
        );
    }
}
