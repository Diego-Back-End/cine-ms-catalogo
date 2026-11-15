package com.cine.ms_catalogo.dto;

import java.time.LocalDateTime;

public record FuncionResponse(
        Long id,
        Long salaId,
        Long sucursalId,
        LocalDateTime fechaHora,
        String formato,
        String idioma,
        Long peliculaId,
        String peliculaTitulo
) {}
