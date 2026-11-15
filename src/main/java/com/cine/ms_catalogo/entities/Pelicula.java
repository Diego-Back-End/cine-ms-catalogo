package com.cine.ms_catalogo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "peliculas", uniqueConstraints = @UniqueConstraint(columnNames = "slug"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String titulo;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000)
    private String sinopsis;

    @Column(name = "duracion_minutos", nullable = false)
    @NotNull
    @Min(1)
    private Integer duracionMinutos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clasificacion_id")
    private Clasificacion clasificacion;

    // Mantener String para compatibilidad si clasificacion es null (fallback)
    @Column(name = "clasificacion_string", length = 50)
    private String clasificacionString;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String estado;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "peliculas_generos",
        joinColumns = @JoinColumn(name = "pelicula_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private List<Genero> generos = new ArrayList<>();

    @OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Funcion> funciones = new ArrayList<>();
}
