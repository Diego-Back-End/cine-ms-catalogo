package com.cine.ms_catalogo.repositories;

import com.cine.ms_catalogo.entities.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    boolean existsByAdminEmail(String adminEmail);
}
