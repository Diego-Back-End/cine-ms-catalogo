package com.cine.ms_catalogo.controllers;

import com.cine.ms_catalogo.entities.Genero;
import com.cine.ms_catalogo.repositories.GeneroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/generos")
@CrossOrigin(origins = "http://localhost:5173")
public class GeneroController {

    @Autowired
    private GeneroRepository generoRepository;

    @GetMapping
    public ResponseEntity<List<String>> getAllGeneros() {
        List<String> nombres = generoRepository.findAll().stream()
                .map(Genero::getNombre).sorted().toList();
        return ResponseEntity.ok(nombres);
    }

    @PostMapping
    public ResponseEntity<?> createGenero(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "nombre es requerido"));
        }
        String clean = nombre.trim();
        if (generoRepository.existsByNombreIgnoreCase(clean)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Género ya existe"));
        }
        Genero genero = generoRepository.save(new Genero(null, clean));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("nombre", genero.getNombre()));
    }
}
