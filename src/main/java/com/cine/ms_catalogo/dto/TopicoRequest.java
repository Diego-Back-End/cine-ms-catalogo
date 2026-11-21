package com.cine.ms_catalogo.dto;

public record TopicoRequest(
        String nombre,
        Integer particiones,
        Short replicationFactor,
        Long retencionMs
) {
    public int particionesOrDefault() {
        return particiones == null ? 1 : particiones;
    }

    public short replicationFactorOrDefault() {
        return replicationFactor == null ? (short) 1 : replicationFactor;
    }
}
