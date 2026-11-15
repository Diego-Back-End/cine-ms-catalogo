package com.cine.ms_catalogo.controllers;

import com.cine.ms_catalogo.dto.SucursalRequest;
import com.cine.ms_catalogo.dto.SucursalResponse;
import com.cine.ms_catalogo.services.SucursalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> getAllSucursales() {
        List<SucursalResponse> sucursales = sucursalService.obtenerTodas();
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> getSucursalById(@PathVariable Long id) {
        return sucursalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SucursalResponse> createSucursal(@Valid @RequestBody SucursalRequest request) {
        SucursalResponse saved = sucursalService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable Long id, @Valid @RequestBody SucursalRequest request) {
        return sucursalService.actualizarAdmin(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
