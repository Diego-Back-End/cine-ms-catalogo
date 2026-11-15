package com.cine.ms_catalogo.repositories;

import com.cine.ms_catalogo.entities.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    Optional<Pelicula> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
