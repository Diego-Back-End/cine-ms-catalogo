package com.cine.ms_catalogo.controllers;

import com.cine.ms_catalogo.entities.Clasificacion;
import com.cine.ms_catalogo.repositories.ClasificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clasificaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class ClasificacionController {

    @Autowired
    private ClasificacionRepository clasificacionRepository;

    @GetMapping
    public ResponseEntity<List<String>> getAllClasificaciones() {
        List<String> nombres = clasificacionRepository.findAll().stream()
                .map(Clasificacion::getNombre).sorted().toList();
        return ResponseEntity.ok(nombres);
    }

    @PostMapping
    public ResponseEntity<?> createClasificacion(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "nombre es requerido"));
        }
        String clean = nombre.trim();
        if (clasificacionRepository.existsByNombreIgnoreCase(clean)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Clasificación ya existe"));
        }
        Clasificacion clasif = clasificacionRepository.save(new Clasificacion(null, clean));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("nombre", clasif.getNombre()));
    }
}
