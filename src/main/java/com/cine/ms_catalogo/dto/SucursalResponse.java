package com.cine.ms_catalogo.dto;

public record SucursalResponse(
        Long id,
        String nombre,
        String direccion,
        String ciudad,
        Integer cantidadSalas,
        String tiposSala,
        String adminEmail
) {}
