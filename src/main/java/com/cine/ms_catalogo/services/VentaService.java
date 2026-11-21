package com.cine.ms_catalogo.services;

import com.cine.ms_catalogo.dto.VentaResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class VentaService {

    private final CopyOnWriteArrayList<VentaResponse> ventas = new CopyOnWriteArrayList<>();

    public void registrar(VentaResponse venta) {
        if (venta != null) {
            ventas.add(venta);
        }
    }

    public List<VentaResponse> listar() {
        List<VentaResponse> copia = new ArrayList<>(ventas);
        Collections.reverse(copia);
        return copia;
    }
}
