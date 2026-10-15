package com.springboot.app.apiminimarket.mapper;

import com.springboot.app.apiminimarket.dto.CategoriaRequest;
import com.springboot.app.apiminimarket.dto.CategoriaResponse;
import com.springboot.app.apiminimarket.model.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    //Convertir a response
    public CategoriaResponse toResponse(Categoria categoria){
        if(categoria == null) return null;

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.isActivo()
        );
    }

    //Convertir a entidad
    public Categoria toEntity(CategoriaRequest request){
        if (request == null) return null;

        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        return categoria;
    }
}
