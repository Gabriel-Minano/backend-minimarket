package com.springboot.app.apiminimarket.repository;

import com.springboot.app.apiminimarket.model.Usuario;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //Paginación con filtrado
    Page<Usuario> findByActivo(boolean activo, Pageable pageable);

    //Busqueda por email, luego servirá para el Spring Security
    Optional<Usuario> findByEmail(String email);

    @Override
    @Query(value = "SELECT u FROM Usuario u",
            countQuery = "SELECT count(u) FROM Usuario u")
    Page<Usuario> findAll(Pageable pageable);

    @Query(value = "SELECT u FROM Usuario u WHERE u.activo = :activo",
            countQuery = "SELECT count(u) FROM Usuario u WHERE u.activo = :activo")
    Page<Usuario> findByActivoWithQuery(@Param("activo") boolean activo, Pageable pageable);
}
