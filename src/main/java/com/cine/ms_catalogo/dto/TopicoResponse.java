package com.cine.ms_catalogo.dto;

public record TopicoResponse(
        String nombre,
        int particiones,
        short replicationFactor
) {
}
