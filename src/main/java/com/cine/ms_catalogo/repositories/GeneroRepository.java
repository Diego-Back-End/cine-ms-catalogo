package com.cine.ms_catalogo.repositories;

import com.cine.ms_catalogo.entities.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
}
