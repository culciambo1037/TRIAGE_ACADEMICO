package com.uniquindio.triage.service;

import com.uniquindio.triage.dto.response.HistorialDTO;
import com.uniquindio.triage.entity.HistorialSolicitud;
import com.uniquindio.triage.entity.Solicitud;
import com.uniquindio.triage.entity.Usuario;
import com.uniquindio.triage.exception.NotFoundException;
import com.uniquindio.triage.repository.HistorialSolicitudRepository;
import com.uniquindio.triage.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialSolicitudRepository historialRepository;
    private final SolicitudRepository solicitudRepository;

    // Llamado internamente por SolicitudService — nunca desde el Controller
    public void registrar(Solicitud solicitud, Usuario usuarioActual, String accion, String observaciones) {
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setSolicitud(solicitud);
        historial.setUsuario(usuarioActual);
        historial.setAccionRealizada(accion);
        historial.setObservaciones(observaciones);
        historialRepository.save(historial);
    }

    // Para GET /api/solicitudes/{id}/historial (RF-06)
    public List<HistorialDTO> obtenerPorSolicitud(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));

        return historialRepository
            .findBySolicitudOrderByFechaAccionAsc(solicitud)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    // Convierte entidad → DTO
    private HistorialDTO toDTO(HistorialSolicitud h) {
        return new HistorialDTO(
            h.getId(),
            h.getAccionRealizada(),
            h.getObservaciones(),
            h.getFechaAccion(),
            h.getUsuario().getNombre() + " " + h.getUsuario().getApellido()
        );
    }
}