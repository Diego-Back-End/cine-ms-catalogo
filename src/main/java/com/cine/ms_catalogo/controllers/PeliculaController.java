package com.cine.ms_catalogo.controllers;

import com.cine.ms_catalogo.entities.Pelicula;
import com.cine.ms_catalogo.services.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<Pelicula>> getAllPeliculas() {
        List<Pelicula> peliculas = peliculaService.findAll();
        return ResponseEntity.ok(peliculas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> getPeliculaById(@PathVariable Long id) {
        return peliculaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pelicula> createPelicula(@RequestBody Pelicula pelicula) {
        Pelicula savedPelicula = peliculaService.save(pelicula);
        return ResponseEntity.ok(savedPelicula);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelicula(@PathVariable Long id) {
        peliculaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
