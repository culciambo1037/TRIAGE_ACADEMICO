package com.uniquindio.triage.service;

import com.uniquindio.triage.dto.request.*;
import com.uniquindio.triage.dto.response.*;
import com.uniquindio.triage.entity.*;
import com.uniquindio.triage.enums.*;
import com.uniquindio.triage.exception.*;
import com.uniquindio.triage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;
    private final HistorialService historialService;

    // POST /api/solicitudes (RF-01)
    @Transactional
    public SolicitudDTO crear(SolicitudRequest request, Usuario usuarioActual) {

        Usuario solicitante = usuarioService.buscarEntidadPorId(request.getSolicitanteId());

        Solicitud solicitud = new Solicitud();
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setTipoSolicitud(request.getTipoSolicitud());
        solicitud.setCanalOrigen(request.getCanalOrigen());
        solicitud.setSolicitante(solicitante);
        solicitud.setEstado(EstadoSolicitud.REGISTRADA);
        solicitud.setPrioridad(Prioridad.MEDIA);

        solicitudRepository.save(solicitud);

        historialService.registrar(
            solicitud, usuarioActual,
            "Solicitud registrada",
            "Canal: " + request.getCanalOrigen()
        );

        return toDTO(solicitud);
    }

    // GET /api/solicitudes (RF-07)
    public PageSolicitudDTO listar(EstadoSolicitud estado, TipoSolicitud tipo,
                                   Prioridad prioridad, UUID responsableId,
                                   int page, int size) {
        Usuario responsable = null;
        if (responsableId != null) {
            responsable = usuarioService.buscarEntidadPorId(responsableId);
        }

        Page<Solicitud> pagina = solicitudRepository.filtrar(
            estado, tipo, prioridad, responsable,
            PageRequest.of(page, size)
        );

        return new PageSolicitudDTO(
            pagina.getContent().stream().map(this::toDTO).toList(),
            pagina.getTotalElements(),
            pagina.getTotalPages(),
            pagina.getNumber(),
            pagina.getSize()
        );
    }

    // GET /api/solicitudes/{id} (RF-07)
    public SolicitudDTO obtener(UUID id, Usuario usuarioActual) {
        Solicitud solicitud = buscarEntidad(id);

        // ESTUDIANTE solo puede ver sus propias solicitudes
        if (usuarioActual.getRol() == RolUsuario.ESTUDIANTE) {
            if (!solicitud.getSolicitante().getId().equals(usuarioActual.getId())) {
                throw new ForbiddenException("Solo puedes consultar tus propias solicitudes");
            }
        }

        return toDTO(solicitud);
    }

    // PATCH /api/solicitudes/{id}/clasificar (RF-02, RF-03)
    @Transactional
    public SolicitudDTO clasificar(UUID id, ClasificarRequest request, Usuario usuarioActual) {
        Solicitud solicitud = buscarEntidad(id);

        // Validación de transición
        if (solicitud.getEstado() != EstadoSolicitud.REGISTRADA) {
            throw new ConflictException("Solo se puede clasificar una solicitud en estado REGISTRADA");
        }

        solicitud.setTipoSolicitud(request.getTipoSolicitud());
        solicitud.setPrioridad(request.getPrioridad());
        solicitud.setJustificacionPrioridad(request.getJustificacionPrioridad());
        solicitud.setEstado(EstadoSolicitud.CLASIFICADA);

        solicitudRepository.save(solicitud);

        historialService.registrar(
            solicitud, usuarioActual,
            "Solicitud clasificada",
            "Tipo: " + request.getTipoSolicitud() + " | Prioridad: " + request.getPrioridad()
        );

        return toDTO(solicitud);
    }

    // PATCH /api/solicitudes/{id}/asignar (RF-05)
    @Transactional
    public SolicitudDTO asignar(UUID id, AsignarRequest request, Usuario usuarioActual) {
        Solicitud solicitud = buscarEntidad(id);

        // Validación de transición
        if (solicitud.getEstado() != EstadoSolicitud.CLASIFICADA) {
            throw new ConflictException("Solo se puede asignar una solicitud en estado CLASIFICADA");
        }

        Usuario responsable = usuarioService.buscarEntidadPorId(request.getResponsableId());

        // Validación de responsable activo
        if (!responsable.getActivo()) {
            throw new ConflictException("El responsable seleccionado no está activo");
        }

        solicitud.setResponsable(responsable);
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);

        solicitudRepository.save(solicitud);

        historialService.registrar(
            solicitud, usuarioActual,
            "Responsable asignado",
            "Responsable: " + responsable.getNombre() + " " + responsable.getApellido()
        );

        return toDTO(solicitud);
    }

    // PATCH /api/solicitudes/{id}/estado (RF-04)
    @Transactional
    public SolicitudDTO cambiarEstado(UUID id, CambiarEstadoRequest request, Usuario usuarioActual) {
        Solicitud solicitud = buscarEntidad(id);

        // No se puede modificar una solicitud CERRADA
        if (solicitud.getEstado() == EstadoSolicitud.CERRADA) {
            throw new ConflictException("La solicitud está cerrada y no puede modificarse");
        }

        String accionAnterior = solicitud.getEstado().name();
        solicitud.setEstado(request.getNuevoEstado());

        solicitudRepository.save(solicitud);

        historialService.registrar(
            solicitud, usuarioActual,
            "Cambio de estado: " + accionAnterior + " → " + request.getNuevoEstado(),
            request.getObservaciones()
        );

        return toDTO(solicitud);
    }

    // PATCH /api/solicitudes/{id}/cerrar (RF-08)
    @Transactional
    public SolicitudDTO cerrar(UUID id, CerrarRequest request, Usuario usuarioActual) {
        Solicitud solicitud = buscarEntidad(id);

        // Validación de transición
        if (solicitud.getEstado() != EstadoSolicitud.ATENDIDA) {
            throw new ConflictException("Solo se puede cerrar una solicitud en estado ATENDIDA");
        }

        solicitud.setEstado(EstadoSolicitud.CERRADA);
        solicitud.setObservacionCierre(request.getObservacionCierre());
        solicitud.setFechaCierre(LocalDateTime.now());

        solicitudRepository.save(solicitud);

        historialService.registrar(
            solicitud, usuarioActual,
            "Solicitud cerrada",
            request.getObservacionCierre()
        );

        return toDTO(solicitud);
    }

    // ── Métodos internos ──────────────────────────────────────
    public Solicitud buscarEntidad(UUID id) {
        return solicitudRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));
    }

    private SolicitudDTO toDTO(Solicitud s) {
        return new SolicitudDTO(
            s.getId(),
            s.getDescripcion(),
            s.getEstado(),
            s.getTipoSolicitud(),
            s.getPrioridad(),
            s.getCanalOrigen(),
            s.getJustificacionPrioridad(),
            s.getObservacionCierre(),
            s.getFechaRegistro(),
            s.getFechaCierre(),
            s.getSolicitante().getNombre() + " " + s.getSolicitante().getApellido(),
            s.getResponsable() != null
                ? s.getResponsable().getNombre() + " " + s.getResponsable().getApellido()
                : null
        );
    }
}