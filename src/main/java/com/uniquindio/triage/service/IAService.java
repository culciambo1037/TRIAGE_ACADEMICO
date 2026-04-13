package com.uniquindio.triage.service;

import com.uniquindio.triage.dto.response.ResumenDTO;
import com.uniquindio.triage.dto.response.SugerenciaDTO;
import com.uniquindio.triage.entity.Solicitud;
import com.uniquindio.triage.entity.SugerenciaIA;
import com.uniquindio.triage.enums.Prioridad;
import com.uniquindio.triage.enums.TipoSolicitud;
import com.uniquindio.triage.exception.NotFoundException;
import com.uniquindio.triage.repository.SolicitudRepository;
import com.uniquindio.triage.repository.SugerenciaIARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IAService {

    private final SolicitudRepository solicitudRepository;
    private final SugerenciaIARepository sugerenciaIARepository;

    // POST /api/ia/sugerir (RF-10)
    @Transactional
    public SugerenciaDTO sugerir(String descripcion) {
        TipoSolicitud tipo      = inferirTipo(descripcion);
        Prioridad     prioridad = inferirPrioridad(descripcion);
        return new SugerenciaDTO(tipo, prioridad, false);
    }

    // POST /api/ia/resumen/{id} (RF-09)
    @Transactional
    public ResumenDTO generarResumen(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));

        String resumen = String.format(
            "Solicitud de tipo %s registrada por canal %s con prioridad %s. " +
            "Estado actual: %s. Descripción: %s",
            solicitud.getTipoSolicitud(),
            solicitud.getCanalOrigen(),
            solicitud.getPrioridad(),
            solicitud.getEstado(),
            solicitud.getDescripcion().length() > 100
                ? solicitud.getDescripcion().substring(0, 100) + "..."
                : solicitud.getDescripcion()
        );

        SugerenciaIA sugerencia = sugerenciaIARepository
            .findBySolicitud(solicitud)
            .orElse(new SugerenciaIA());

        sugerencia.setSolicitud(solicitud);
        sugerencia.setTipoSugerido(solicitud.getTipoSolicitud());
        sugerencia.setPrioridadSugerida(solicitud.getPrioridad());
        sugerencia.setResumenGenerado(resumen);
        sugerenciaIARepository.save(sugerencia);

        return new ResumenDTO(resumen);
    }

    // ── Lógica mock de inferencia ─────────────────────────────
    private TipoSolicitud inferirTipo(String descripcion) {
        String d = descripcion.toLowerCase();
        if (d.contains("registro") || d.contains("matricul") || d.contains("asignatura"))
            return TipoSolicitud.REGISTRO_ASIGNATURAS;
        else if (d.contains("homolog") || d.contains("convalid"))
            return TipoSolicitud.HOMOLOGACION;
        else if (d.contains("cancel") || d.contains("retiro"))
            return TipoSolicitud.CANCELACION_ASIGNATURA;
        else if (d.contains("cupo") || d.contains("sobrecupo"))
            return TipoSolicitud.SOLICITUD_CUPOS;
        else if (d.contains("consult") || d.contains("informaci"))
            return TipoSolicitud.CONSULTA_ACADEMICA;
        else
            return TipoSolicitud.OTRO;
    }

    private Prioridad inferirPrioridad(String descripcion) {
        String d = descripcion.toLowerCase();
        if (d.contains("urgente") || d.contains("critico") || d.contains("inmediato"))
            return Prioridad.CRITICA;
        else if (d.contains("importante") || d.contains("pronto") || d.contains("vence"))
            return Prioridad.ALTA;
        else if (d.contains("cuando pueda") || d.contains("no urgente"))
            return Prioridad.BAJA;
        else
            return Prioridad.MEDIA;
    }
}