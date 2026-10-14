package com.springboot.app.apiminimarket.repository;

import com.springboot.app.apiminimarket.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    //Paginación con filtro
    Page<Categoria> findByActivo(boolean activo, Pageable pageable);

    //Búsqueda por nombre
    Optional<Categoria> findByNombre(String nombre);
}
