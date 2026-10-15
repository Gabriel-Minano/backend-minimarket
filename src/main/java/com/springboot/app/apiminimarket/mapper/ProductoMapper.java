package com.springboot.app.apiminimarket.mapper;

import com.springboot.app.apiminimarket.dto.ProductoResponse;
import com.springboot.app.apiminimarket.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    //Convertir a response
    public ProductoResponse toResponse(Producto producto){
        if(producto == null) return null;

        Long idCategoria = (producto.getCategoria() != null) ? producto.getCategoria().getId() : null;
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                idCategoria,
                producto.isActivo()
        );
    }
}
