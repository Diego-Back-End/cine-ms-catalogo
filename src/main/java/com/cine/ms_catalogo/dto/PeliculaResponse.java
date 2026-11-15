package com.cine.ms_catalogo.dto;

import java.util.List;

public record PeliculaResponse(
        Long id,
        String titulo,
        String slug,
        String sinopsis,
        Integer duracion,
        String genero,
        List<String> generos,
        String clasificacion,
        String estado,
        String poster
) {}
