package com.cine.ms_catalogo.services;

import com.cine.ms_catalogo.dto.PeliculaRequest;
import com.cine.ms_catalogo.dto.PeliculaResponse;
import com.cine.ms_catalogo.entities.Clasificacion;
import com.cine.ms_catalogo.entities.Genero;
import com.cine.ms_catalogo.entities.Pelicula;
import com.cine.ms_catalogo.repositories.ClasificacionRepository;
import com.cine.ms_catalogo.repositories.GeneroRepository;
import com.cine.ms_catalogo.repositories.PeliculaRepository;
import com.cine.ms_catalogo.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;
    @Autowired
    private GeneroRepository generoRepository;
    @Autowired
    private ClasificacionRepository clasificacionRepository;

    private static final List<String> ESTADOS_VALIDOS = List.of(
            "Estreno", "Pre-venta", "En cartelera", "Próximamente", "No disponible"
    );

    public List<PeliculaResponse> obtenerTodas(String search, String genero, String clasificacion, String estado) {
        List<Pelicula> peliculas = peliculaRepository.findAll();
        return peliculas.stream()
                .filter(p -> search == null || search.isBlank() ||
                        p.getTitulo().toLowerCase().contains(search.toLowerCase()))
                .filter(p -> genero == null || genero.isBlank() ||
                        p.getGeneros().stream().anyMatch(g -> g.getNombre().equalsIgnoreCase(genero)))
                .filter(p -> clasificacion == null || clasificacion.isBlank() ||
                        (p.getClasificacion() != null && p.getClasificacion().getNombre().equalsIgnoreCase(clasificacion)) ||
                        (p.getClasificacionString() != null && p.getClasificacionString().equalsIgnoreCase(clasificacion)))
                .filter(p -> estado == null || estado.isBlank() ||
                        p.getEstado().equalsIgnoreCase(estado))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<PeliculaResponse> findById(Long id) {
        return peliculaRepository.findById(id).map(this::toResponse);
    }

    public Optional<PeliculaResponse> findBySlug(String slug) {
        return peliculaRepository.findBySlug(slug).map(this::toResponse);
    }

    public PeliculaResponse crear(PeliculaRequest req) {
        Pelicula pelicula = new Pelicula();
        applyRequest(pelicula, req);
        pelicula.setSlug(generateUniqueSlug(req.titulo()));
        Pelicula saved = peliculaRepository.save(pelicula);
        return toResponse(saved);
    }

    public Optional<PeliculaResponse> actualizar(Long id, PeliculaRequest req) {
        return peliculaRepository.findById(id).map(pelicula -> {
            // Si cambia título, regenerar slug
            if (!pelicula.getTitulo().equalsIgnoreCase(req.titulo())) {
                pelicula.setSlug(generateUniqueSlug(req.titulo()));
            }
            applyRequest(pelicula, req);
            return toResponse(peliculaRepository.save(pelicula));
        });
    }

    public void deleteById(Long id) {
        peliculaRepository.deleteById(id);
    }

    private void applyRequest(Pelicula pelicula, PeliculaRequest req) {
        pelicula.setTitulo(req.titulo().trim());
        pelicula.setSinopsis(req.sinopsis() != null ? req.sinopsis().trim() : null);
        pelicula.setDuracionMinutos(req.duracion());
        pelicula.setEstado(req.estado().trim());
        pelicula.setImagenUrl(req.poster() != null ? req.poster().trim() : null);

        // Genero: soporta múltiples a futuro, hoy uno
        Genero genero = generoRepository.findByNombreIgnoreCase(req.genero().trim())
                .orElseGet(() -> generoRepository.save(new Genero(null, req.genero().trim())));
        pelicula.getGeneros().clear();
        pelicula.getGeneros().add(genero);

        // Clasificación: tabla dinámica, sin delete
        Clasificacion clasif = clasificacionRepository.findByNombreIgnoreCase(req.clasificacion().trim())
                .orElseGet(() -> clasificacionRepository.save(new Clasificacion(null, req.clasificacion().trim())));
        pelicula.setClasificacion(clasif);
        pelicula.setClasificacionString(clasif.getNombre());
    }

    private String generateUniqueSlug(String titulo) {
        String base = SlugUtils.slugify(titulo);
        String slug = base;
        int suffix = 1;
        while (peliculaRepository.existsBySlug(slug)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }

    private PeliculaResponse toResponse(Pelicula p) {
        String clasifName = p.getClasificacion() != null ? p.getClasificacion().getNombre()
                : p.getClasificacionString();
        List<String> generosNombres = p.getGeneros().stream()
                .map(Genero::getNombre).collect(Collectors.toList());
        String firstGenero = generosNombres.isEmpty() ? null : generosNombres.get(0);
        return new PeliculaResponse(
                p.getId(),
                p.getTitulo(),
                p.getSlug(),
                p.getSinopsis(),
                p.getDuracionMinutos(),
                firstGenero,
                generosNombres,
                clasifName,
                p.getEstado(),
                p.getImagenUrl()
        );
    }
}
