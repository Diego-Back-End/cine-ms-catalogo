package com.cine.ms_catalogo.dto;

import java.util.List;

public record VentaResponse(
        String reservaId,
        String email,
        Long funcionId,
        List<String> asientos,
        Double total,
        String fecha
) {
}
