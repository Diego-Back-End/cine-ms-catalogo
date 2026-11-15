package com.cine.ms_catalogo.services;

import com.cine.ms_catalogo.dto.SucursalRequest;
import com.cine.ms_catalogo.dto.SucursalResponse;
import com.cine.ms_catalogo.entities.Sucursal;
import com.cine.ms_catalogo.repositories.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    public List<SucursalResponse> obtenerTodas() {
        return sucursalRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<SucursalResponse> findById(Long id) {
        return sucursalRepository.findById(id).map(this::toResponse);
    }

    public SucursalResponse crear(SucursalRequest req) {
        if (sucursalRepository.existsByAdminEmail(req.adminEmail())) {
            throw new IllegalArgumentException("Ya existe una sucursal con ese administrador");
        }
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(req.nombre().trim());
        sucursal.setDireccion(req.direccion().trim());
        sucursal.setCiudad(req.ciudad().trim());
        sucursal.setCantidadSalas(req.cantidadSalas());
        sucursal.setTiposSala(req.tiposSala());
        sucursal.setAdminEmail(req.adminEmail().trim().toLowerCase());
        return toResponse(sucursalRepository.save(sucursal));
    }

    public Optional<SucursalResponse> actualizarAdmin(Long id, SucursalRequest req) {
        return sucursalRepository.findById(id).map(sucursal -> {
            // Solo se permite editar el adminEmail
            String nuevoEmail = req.adminEmail().trim().toLowerCase();
            if (!nuevoEmail.equals(sucursal.getAdminEmail())
                    && sucursalRepository.existsByAdminEmail(nuevoEmail)) {
                throw new IllegalArgumentException("Ya existe una sucursal con ese administrador");
            }
            sucursal.setAdminEmail(nuevoEmail);
            return toResponse(sucursalRepository.save(sucursal));
        });
    }

    private SucursalResponse toResponse(Sucursal s) {
        return new SucursalResponse(
                s.getId(),
                s.getNombre(),
                s.getDireccion(),
                s.getCiudad(),
                s.getCantidadSalas(),
                s.getTiposSala(),
                s.getAdminEmail()
        );
    }
}
