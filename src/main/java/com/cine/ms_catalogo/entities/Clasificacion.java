package com.cine.ms_catalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clasificaciones", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String nombre;
}
