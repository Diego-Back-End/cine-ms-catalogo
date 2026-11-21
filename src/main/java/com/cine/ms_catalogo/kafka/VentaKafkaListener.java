package com.cine.ms_catalogo.kafka;

import com.cine.ms_catalogo.dto.VentaResponse;
import com.cine.ms_catalogo.services.VentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class VentaKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(VentaKafkaListener.class);

    private final VentaService ventaService;
    private final JsonMapper jsonMapper;

    public VentaKafkaListener(VentaService ventaService) {
        this.ventaService = ventaService;
        this.jsonMapper = JsonMapper.builder().build();
    }

    @KafkaListener(topics = "reserva-confirmada", groupId = "ms-catalogo-ventas")
    public void consumir(String mensaje) {
        String reservaId = null;
        try {
            VentaResponse venta = jsonMapper.readValue(mensaje, VentaResponse.class);
            reservaId = venta != null ? venta.reservaId() : null;
            ventaService.registrar(venta);
        } catch (Exception e) {
            log.error("Error al procesar venta reservaId={}", reservaId, e);
        }
    }
}
