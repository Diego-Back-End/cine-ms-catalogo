package com.cine.ms_catalogo.dto;

import jakarta.validation.constraints.*;

public record PeliculaRequest(
        @NotBlank @Size(max = 100) String titulo,
        @Size(max = 1000) String sinopsis,
        @NotNull @Min(1) Integer duracion,
        @NotBlank String genero,
        @NotBlank String clasificacion,
        @NotBlank String estado,
        @Size(max = 500) String poster
) {}
