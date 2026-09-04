package com.cine.ms_catalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "peliculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(nullable = false, length = 50)
    private String clasificacion;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(nullable = false, length = 50)
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
