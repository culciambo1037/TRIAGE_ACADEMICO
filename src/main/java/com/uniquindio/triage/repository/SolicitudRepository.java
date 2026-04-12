package com.uniquindio.triage.repository;

import com.uniquindio.triage.entity.Solicitud;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.enums.EstadoSolicitud;
import com.uniquindio.triage.enums.Prioridad;
import com.uniquindio.triage.enums.TipoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, UUID> {

    // Para GET /api/solicitudes con filtros y paginación (RF-07)
    @Query("""
        SELECT s FROM Solicitud s
        WHERE (:estado    IS NULL OR s.estado        = :estado)
        AND   (:tipo      IS NULL OR s.tipoSolicitud = :tipo)
        AND   (:prioridad IS NULL OR s.prioridad     = :prioridad)
        AND   (:responsable IS NULL OR s.responsable = :responsable)
        """)
    Page<Solicitud> filtrar(
        @Param("estado")      EstadoSolicitud estado,
        @Param("tipo")        TipoSolicitud tipo,
        @Param("prioridad")   Prioridad prioridad,
        @Param("responsable") Usuario responsable,
        Pageable pageable
    );
}