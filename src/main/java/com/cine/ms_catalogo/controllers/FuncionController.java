package com.cine.ms_catalogo.controllers;

import com.cine.ms_catalogo.dto.FuncionRequest;
import com.cine.ms_catalogo.dto.FuncionResponse;
import com.cine.ms_catalogo.services.FuncionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    @GetMapping
    public ResponseEntity<List<FuncionResponse>> getFunciones(
            @RequestParam(required = false) Long sucursalId) {
        List<FuncionResponse> funciones = funcionService.obtenerTodas(sucursalId);
        return ResponseEntity.ok(funciones);
    }

    @PostMapping
    public ResponseEntity<FuncionResponse> createFuncion(@Valid @RequestBody FuncionRequest request) {
        FuncionResponse saved = funcionService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/limpiar-vencidas")
    public ResponseEntity<Integer> limpiarFuncionesVencidas(
            @RequestParam(required = false) Long sucursalId) {
        int eliminadas = funcionService.limpiarFuncionesVencidas(sucursalId);
        return ResponseEntity.ok(eliminadas);
    }
}
