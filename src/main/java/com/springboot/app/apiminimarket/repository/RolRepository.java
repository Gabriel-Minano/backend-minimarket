package com.springboot.app.apiminimarket.repository;

import com.springboot.app.apiminimarket.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
}
