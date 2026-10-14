package com.springboot.app.apiminimarket.repository;

import com.springboot.app.apiminimarket.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    //Buscar detalles de una venta específica
    List<DetalleVenta> findByVentaId(Long ventaId);
}
