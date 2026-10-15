package com.springboot.app.apiminimarket.mapper;

import com.springboot.app.apiminimarket.dto.DetalleVentaResponse;
import com.springboot.app.apiminimarket.dto.VentaResponse;
import com.springboot.app.apiminimarket.model.Venta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VentaMapper {
    public VentaResponse toResponse(Venta venta) {
        if (venta == null) return null;

        //Mapear la lista de detalles de la entidad a DTOs de respuesta
        List<DetalleVentaResponse> detallesResponse = venta.getDetalles().stream()
                .map(det -> new DetalleVentaResponse(
                        det.getId(),
                        det.getProducto().getId(),
                        det.getProducto().getNombre(),
                        det.getCantidad(),
                        det.getPrecioUnitario(),
                        det.getSubtotal()
                ))
                .collect(Collectors.toList());

        String nombreUsuario = (venta.getUsuario() != null)
                ? venta.getUsuario().getNombre() + " " + venta.getUsuario().getApellido()
                : null;

        return new VentaResponse(
                venta.getId(),
                venta.getUsuario() != null ? venta.getUsuario().getId() : null,
                nombreUsuario,
                venta.getFechaVenta(),
                venta.getTotal(),
                venta.isActivo(),
                detallesResponse
        );
    }
}
