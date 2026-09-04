package com.cine.ms_catalogo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
@CrossOrigin(origins = "http://localhost:5173")
public class EstadoController {

    private static final List<String> ESTADOS = List.of(
            "Estreno", "Pre-venta", "En cartelera", "Próximamente", "No disponible"
    );

    @GetMapping
    public ResponseEntity<List<String>> getAllEstados() {
        return ResponseEntity.ok(ESTADOS);
    }
}
