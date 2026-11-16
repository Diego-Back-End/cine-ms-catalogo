package com.cine.ms_catalogo.services;

import com.cine.ms_catalogo.dto.FuncionRequest;
import com.cine.ms_catalogo.dto.FuncionResponse;
import com.cine.ms_catalogo.entities.Funcion;
import com.cine.ms_catalogo.entities.Pelicula;
import com.cine.ms_catalogo.repositories.FuncionRepository;
import com.cine.ms_catalogo.repositories.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    // Margen entre funciones (en minutos)
    private static final int MARGEN_MINUTOS = 30;

    public List<FuncionResponse> obtenerTodas(Long sucursalId) {
        List<Funcion> funciones = (sucursalId != null)
                ? funcionRepository.findBySucursalId(sucursalId)
                : funcionRepository.findAll();
        return funciones.stream().map(this::toResponse).toList();
    }

    public FuncionResponse crear(FuncionRequest req) {
        Pelicula pelicula = peliculaRepository.findById(req.peliculaId())
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

        // Validar solapamiento: misma sala, misma sucursal, fechas que se solapan
        // considerando duración de película + margen. Fórmula bidireccional:
        // existing.inicio < fin_nuevo && inicio_nuevo < existing.fin
        LocalDateTime inicio = req.fechaHora();
        LocalDateTime fin = inicio.plusMinutes(pelicula.getDuracionMinutos() + MARGEN_MINUTOS);

        List<Funcion> funcionesSala = funcionRepository.findBySucursalId(req.sucursalId())
                .stream()
                .filter(f -> f.getSalaId().equals(req.salaId()))
                .toList();

        for (Funcion existente : funcionesSala) {
            LocalDateTime existenteInicio = existente.getFechaHora();
            Pelicula existentePelicula = existente.getPelicula();
            int existenteDuracion = existentePelicula != null ? existentePelicula.getDuracionMinutos() : 0;
            LocalDateTime existenteFin = existenteInicio.plusMinutes(existenteDuracion + MARGEN_MINUTOS);

            boolean solapa = existenteInicio.isBefore(fin) && inicio.isBefore(existenteFin);
            if (solapa) {
                throw new IllegalArgumentException(
                        "La sala ya tiene una función programada en ese horario (considerando duración de película + margen de 30 min)"
                );
            }
        }

        Funcion funcion = new Funcion();
        funcion.setSalaId(req.salaId());
        funcion.setSucursalId(req.sucursalId());
        funcion.setFechaHora(req.fechaHora());
        funcion.setFormato(req.formato());
        funcion.setIdioma(req.idioma());
        funcion.setPelicula(pelicula);

        return toResponse(funcionRepository.save(funcion));
    }

    public int limpiarFuncionesVencidas(Long sucursalId) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime dentroDe30Dias = ahora.plusDays(30);
        return funcionRepository.limpiarFuncionesVencidas(sucursalId, ahora, dentroDe30Dias);
    }

    private FuncionResponse toResponse(Funcion f) {
        String peliculaTitulo = f.getPelicula() != null ? f.getPelicula().getTitulo() : null;
        return new FuncionResponse(
                f.getId(),
                f.getSalaId(),
                f.getSucursalId(),
                f.getFechaHora(),
                f.getFormato(),
                f.getIdioma(),
                f.getPelicula() != null ? f.getPelicula().getId() : null,
                peliculaTitulo
        );
    }
}
