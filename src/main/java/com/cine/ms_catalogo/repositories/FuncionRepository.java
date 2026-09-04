package com.cine.ms_catalogo.repositories;

import com.cine.ms_catalogo.entities.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
}
