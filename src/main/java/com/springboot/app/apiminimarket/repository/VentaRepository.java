package com.springboot.app.apiminimarket.repository;

import com.springboot.app.apiminimarket.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    //Paginación con filtro
    Page<Venta> findByActivo(boolean activo, Pageable pageable);

    //Paginar ventas realizadas por un usuario
    Page<Venta> findByUsuarioIdAndActivo(Long usuarioId, boolean activo, Pageable pageable);

    //Buscar ventas dentro de un rango de fechas (Luego será usado para reportes)
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.activo = :activo")
    Page<Venta> findByFechaVentaBetweenAndActivo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("activo") boolean activo,
            Pageable pageable
    );

    //Traer una venta junto al usuario y los detalles de venta
    //Evita el error de LazyInitializationException al consultar el detalle completo de una venta
    @Query("SELECT v FROM Venta v JOIN FETCH v.usuario u LEFT JOIN FETCH v.detalles d LEFT JOIN FETCH d.producto p WHERE v.id = :id")
    Optional<Venta> findByIdWithDetails(@Param("id") Long id);
}
