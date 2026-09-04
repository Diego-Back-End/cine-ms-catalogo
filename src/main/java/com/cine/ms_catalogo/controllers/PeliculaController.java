package com.cine.ms_catalogo.controllers;

import com.cine.ms_catalogo.dto.PeliculaRequest;
import com.cine.ms_catalogo.dto.PeliculaResponse;
import com.cine.ms_catalogo.services.PeliculaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
@CrossOrigin(origins = "http://localhost:5173")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<PeliculaResponse>> getAllPeliculas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false, name = "rating") String clasificacion,
            @RequestParam(required = false) String estado) {
        List<PeliculaResponse> peliculas = peliculaService.obtenerTodas(search, genero, clasificacion, estado);
        return ResponseEntity.ok(peliculas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponse> getPeliculaById(@PathVariable Long id) {
        return peliculaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PeliculaResponse> getPeliculaBySlug(@PathVariable String slug) {
        return peliculaService.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PeliculaResponse> createPelicula(@Valid @RequestBody PeliculaRequest request) {
        PeliculaResponse saved = peliculaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeliculaResponse> updatePelicula(@PathVariable Long id, @Valid @RequestBody PeliculaRequest request) {
        return peliculaService.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelicula(@PathVariable Long id) {
        peliculaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
