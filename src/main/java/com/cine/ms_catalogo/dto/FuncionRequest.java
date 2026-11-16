package com.cine.ms_catalogo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record FuncionRequest(
        @NotNull Long salaId,
        @NotNull Long sucursalId,
        @NotNull LocalDateTime fechaHora,
        @NotBlank @Size(max = 50) String formato,
        @NotBlank @Size(max = 50) String idioma,
        @NotNull Long peliculaId
) {}
