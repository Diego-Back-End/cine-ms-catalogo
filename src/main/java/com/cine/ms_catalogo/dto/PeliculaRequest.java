package com.cine.ms_catalogo.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record PeliculaRequest(
        @NotBlank @Size(max = 100) String titulo,
        @Size(max = 1000) String sinopsis,
        @NotNull @Min(1) Integer duracion,
        @Size(min = 1) List<String> generos,
        @NotBlank String clasificacion,
        @NotBlank String estado,
        @Size(max = 500) String poster
) {}
