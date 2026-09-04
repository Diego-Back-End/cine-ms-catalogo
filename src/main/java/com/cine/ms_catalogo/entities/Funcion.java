package com.cine.ms_catalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "funciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sala_id", nullable = false)
    private Long salaId;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 50)
    private String formato;

    @Column(nullable = false, length = 50)
    private String idioma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Pelicula pelicula;
}
