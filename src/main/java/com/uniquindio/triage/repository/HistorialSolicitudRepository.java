package com.uniquindio.triage.repository;

import com.uniquindio.triage.entity.HistorialSolicitud;
import com.uniquindio.triage.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, UUID> {

    // Para GET /api/solicitudes/{id}/historial (RF-06)
    List<HistorialSolicitud> findBySolicitudOrderByFechaAccionAsc(Solicitud solicitud);
}