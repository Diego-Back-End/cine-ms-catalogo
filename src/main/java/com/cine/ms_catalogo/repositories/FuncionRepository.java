package com.cine.ms_catalogo.repositories;

import com.cine.ms_catalogo.entities.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    List<Funcion> findBySucursalId(Long sucursalId);

    @Query("""
        SELECT f FROM Funcion f
        WHERE f.salaId = :salaId
          AND f.sucursalId = :sucursalId
          AND f.fechaHora >= :inicio
          AND f.fechaHora < :fin
    """)
    List<Funcion> findBySalaAndFechaRango(
            @Param("salaId") Long salaId,
            @Param("sucursalId") Long sucursalId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
        DELETE FROM Funcion f
        WHERE f.sucursalId = :sucursalId
          AND f.fechaHora < :fechaLimite
          AND NOT EXISTS (
              SELECT 1 FROM Funcion f2
              WHERE f2.sucursalId = f.sucursalId
                AND f2.fechaHora >= :fechaLimite
                AND f2.fechaHora < :fechaLimiteMas30Dias
          )
    """)
    int limpiarFuncionesVencidas(
            @Param("sucursalId") Long sucursalId,
            @Param("fechaLimite") LocalDateTime fechaLimite,
            @Param("fechaLimiteMas30Dias") LocalDateTime fechaLimiteMas30Dias
    );
}
