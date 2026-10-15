package com.springboot.app.apiminimarket.repository;

import com.springboot.app.apiminimarket.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    //Paginación con filtro
    Page<Producto> findByActivo(boolean activo, Pageable pageable);

    //Paginación por categoría y estado activo
    Page<Producto> findByCategoriaIdAndActivo(Long categoriaId, boolean activo, Pageable pageable);

    //Paginación por nombre with IgnoreCase de minus y mayus
    Page<Producto> findByNombreContainingIgnoreCaseAndActivo(String nombre, boolean activo, Pageable pageable);

    //Busqueda por nombre
    Optional<Producto> findByNombre(String nombre);

    //Para traer el producto junto a su categoría, porque al estar con lazy no se podría
    //de forma normal debería usarse otra consulta adicional
    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE p.id = :id")
    Optional<Producto> findByIdWithCategoria(@Param("id") Long id);
}
