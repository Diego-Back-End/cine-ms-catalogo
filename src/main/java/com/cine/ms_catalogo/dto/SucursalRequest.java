package com.cine.ms_catalogo.dto;

import jakarta.validation.constraints.*;

public record SucursalRequest(
        @NotBlank @Size(max = 200) String nombre,
        @NotBlank @Size(max = 500) String direccion,
        @NotBlank @Size(max = 100) String ciudad,
        @NotNull @Min(1) Integer cantidadSalas,
        @Size(max = 500) String tiposSala,
        @NotBlank @Email @Size(max = 200) String adminEmail
) {}
