package com.cine.ms_catalogo.config;

import com.cine.ms_catalogo.entities.Clasificacion;
import com.cine.ms_catalogo.entities.Genero;
import com.cine.ms_catalogo.repositories.ClasificacionRepository;
import com.cine.ms_catalogo.repositories.GeneroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner seed(GeneroRepository generoRepo, ClasificacionRepository clasifRepo) {
        return args -> {
            List<String> generos = List.of("Acción","Aventura","Ciencia ficción","Comedia","Documental","Drama","Fantasía","Musical","Romance","Suspenso","Terror");
            for (String g : generos) {
                if (!generoRepo.existsByNombreIgnoreCase(g)) {
                    generoRepo.save(new Genero(null, g));
                }
            }
            List<String> clasifs = List.of("G","PG","PG-13","R","NC-17","Unrated");
            for (String c : clasifs) {
                if (!clasifRepo.existsByNombreIgnoreCase(c)) {
                    clasifRepo.save(new Clasificacion(null, c));
                }
            }
        };
    }
}
